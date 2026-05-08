import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

// ai生成
abstract class PublishDataGenTask @Inject constructor(
    private val execOperations: ExecOperations
) : DefaultTask() {

    // 任务输入：开发分支名
    @get:Input
    abstract val devBranch: Property<String>

    // 任务输入：发布分支名
    @get:Input
    abstract val releaseBranch: Property<String>

    // 定义一个内部数据类，用于接收 Git 命令的返回结果
    private data class GitResult(val exitCode: Int, val output: String)

    init {
        group = "publishing"
        description = "自动合并开发分支到发布分支并推送"
    }

    @TaskAction
    fun execute() {
        val expectedDevBranch = devBranch.get()
        val targetReleaseBranch = releaseBranch.get()

        logger.lifecycle("--- 开始执行 PublishDataGen 任务 ---")

        // 1. 检查是否有未提交的更改
        val statusResult = runGit("status", "--porcelain")
        if (statusResult.output.isNotBlank()) {
            logger.warn("⚠️ 存在未提交的更改，跳过后续操作。")
            return
        }

        // 2. 检查当前是否在指定的 devBranch 分支
        val currentBranch = runGit("branch", "--show-current").output
        if (currentBranch != expectedDevBranch) {
            logger.warn("⚠️ 当前分支为 '$currentBranch'，非指定的开发分支 '$expectedDevBranch'，跳过后续操作。")
            return
        }

        // 3. 检查最新提交的提交信息格式 (使用 %s 只获取第一行 subject)
        val latestCommitMsg = runGit("log", "-1", "--pretty=%s").output
        // 正则：以 v 开头，后面跟着数字，数字之间可以有 . 分隔 (如 v1, v1.0, v1.2.3)
        val versionRegex = Regex("^v\\d+(?:\\.\\d+)*$")
        if (!versionRegex.matches(latestCommitMsg)) {
            logger.warn("⚠️ 最新提交信息 '$latestCommitMsg' 不符合 'v版本号' 格式，跳过后续操作。")
            return
        }

        logger.lifecycle("✅ 前置检查通过，准备切换分支并合并...")
        var isBranchSwitched = false

        try {
            // 4. 切换到 releaseBranch
            runGit("checkout", targetReleaseBranch)
            isBranchSwitched = true

            // 5. 将 devBranch 合并到 releaseBranch (允许忽略错误码以便我们自己处理冲突)
            logger.lifecycle("🔄 正在合并 $expectedDevBranch 到 $targetReleaseBranch ...")
            val mergeResult = runGit("merge", expectedDevBranch, ignoreExitValue = true)

            if (mergeResult.exitCode != 0) {
                // 如果退出码不为 0，说明产生了合并冲突或其他合并错误
                logger.error("❌ 发生合并冲突！正在撤销合并...")
                runGit("merge", "--abort")
                logger.warn("⚠️ 合并已撤销，跳过后续操作。")
                return
            }

            // 6. 推送 releaseBranch 到远程仓库
            logger.lifecycle("⬆️ 正在推送到远程仓库...")
            runGit("push", "origin", targetReleaseBranch)
            logger.lifecycle("🎉 推送成功！")

        } finally {
            // 7. 无论合并/推送成功与否（只要切换过分支），最后都切回 devBranch
            if (isBranchSwitched) {
                logger.lifecycle("🔙 正在切回开发分支 $expectedDevBranch ...")
                runGit("checkout", expectedDevBranch)
            }
        }

        logger.lifecycle("--- PublishDataGen 任务执行完毕 ---")
    }

    /**
     * 执行 Git 命令的辅助方法
     * @param args git 后面的参数
     * @param ignoreExitValue 是否忽略错误状态码（如果不忽略，遇到非 0 状态码直接抛异常停止构建）
     */
    private fun runGit(vararg args: String, ignoreExitValue: Boolean = false): GitResult {
        val outStream = ByteArrayOutputStream()
        val errStream = ByteArrayOutputStream()
        var exitValue = 0

        execOperations.exec {
            commandLine("git", *args)
            standardOutput = outStream
            errorOutput = errStream
            // 我们手动接管 exitValue 的处理，以防止 Gradle 直接中断构建
            isIgnoreExitValue = true
        }.let {
            exitValue = it.exitValue
        }

        val output = outStream.toString().trim()
        val error = errStream.toString().trim()

        // 如果不允许忽略错误且执行失败，抛出异常让 Gradle 构建失败
        if (!ignoreExitValue && exitValue != 0) {
            throw GradleException("Git 命令执行失败: git ${args.joinToString(" ")}\n错误信息: $error")
        }

        return GitResult(exitValue, output)
    }
}