tasks.register<GenGitignoreTask>("genGitignore") {
    workDirectory = projectDir
    markerFile = file("${projectDir}/local/.initializedGit")
    generatedDir = file("${projectDir}/src/generated")
    gitignoreFile  = file("${projectDir}/src/generated/.gitignore")
}

tasks.register<PublishDataGenTask>("publishDataGen") {
    group = "publishing"
    devBranch = "dev"
    releaseBranch = "release"
}