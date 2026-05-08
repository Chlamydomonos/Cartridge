tasks.register<GenGitignoreTask>("genGitignore") {
    workDirectory = projectDir
    markerFile = file("${projectDir}/.gradle/.initializedGit")
    generatedDir = file("${projectDir}/src/generated")
    gitignoreFile  = file("${projectDir}/src/generated/.gitignore")
}