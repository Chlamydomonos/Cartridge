
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Console
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import java.util.*
import javax.inject.Inject

abstract class GenGitignoreTask @Inject constructor(
    private val execOperations: ExecOperations
) : DefaultTask() {
    @get:Console
    abstract val workDirectory: RegularFileProperty

    @get:OutputFile
    abstract val markerFile: RegularFileProperty

    @get:OutputDirectory
    abstract val generatedDir: RegularFileProperty

    @get:OutputFile
    abstract val gitignoreFile: RegularFileProperty

    @TaskAction
    fun run() {
        if (markerFile.get().asFile.exists()) {
            println("已经执行过genGitignore，跳过该任务")
            return
        }

        val workDir = workDirectory.asFile.get()

        println("获取远程内容...")
        val fetchSuccess = try {
            val result = execOperations.exec {
                workingDir = workDir
                commandLine("git", "fetch")
                isIgnoreExitValue = true
            }
            result.exitValue == 0
        } catch (_: Exception) {
            false
        }
        if (!fetchSuccess) {
            println("获取失败，取消后续操作")
            return
        }

        println("签出到dev分支...")
        val checkoutSuccess = try {
            val result = execOperations.exec {
                workingDir = workDir
                commandLine("git", "switch", "dev")
                isIgnoreExitValue = true
            }
            result.exitValue == 0
        } catch (_: Exception) {
            false
        }
        if (!checkoutSuccess) {
            println("签出失败，取消后续操作")
            return
        }

        val generated = generatedDir.asFile.get()
        if (generated.exists()) {
            println("删除generated目录...")
            val deleted = generated.deleteRecursively()
            if (!deleted) {
                println("删除失败，取消后续操作")
            }
        }

        println("重建generated目录...")
        val createdDir = generated.mkdirs()
        if (!createdDir) {
            println("重建失败，取消后续操作")
            return
        }

        println("创建.gitignore...")
        val gitignore = gitignoreFile.asFile.get()
        gitignore.writeText("*\n")

        println("记录操作...")
        val marker = markerFile.asFile.get()
        val markerParent = marker.parentFile
        if (!markerParent.exists()) {
            val createdMarkerParent = markerParent.mkdirs()
            if (!createdMarkerParent) {
                println("记录操作失败")
                return
            }
        }
        marker.writeText("Initialized at ${Date()}")
        println("已完成Git项目初始化")
    }
}