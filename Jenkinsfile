#!groovy
build('db-common-lib', 'docker-host') {
    checkoutRepo()
    loadBuildUtils()

    def javaLibPipeline
    runStage('load JavaLib pipeline') {
        javaLibPipeline = load("build_utils/jenkins_lib/pipeJavaLib.groovy")
    }

    def buildImageTag = "b04c5291d101132e53e578d96e1628d2e6dab0c0"
    javaLibPipeline(buildImageTag)
}