# 来自深渊：弹药包

将《来自深渊》中臭名昭著（不）的弹药包（以及上升诅咒系统）加入Minecraft。

## 开发

首次用IDEA打开项目时，Gradle构建工具会自动签出到`dev`分支，并删除`src/generated`目录下的文件。为了正常运行mod，在运行`runClient`前需要先运行一次`runData`。

要发布一个版本，首先把更改合并到`dev`分支，并确保最后一条提交的提交消息形如`v版本号`。然后，运行Gradle任务`publishDataGen`，Gradle会自动完成发布到Github的流程。

注意，`main`分支不应该在本地访问。这会打乱数据生成产生的文件。如果你正常Clone项目并用IDEA打开，Gradle应该会在本地自动删除`main`分支。