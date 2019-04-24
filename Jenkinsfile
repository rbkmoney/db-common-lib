#!groovy
build('db-common-lib', 'docker-host') {
    checkoutRepo()
    loadBuildUtils()

    def javaLibPipeline
    runStage('load JavaLib pipeline') {
        javaLibPipeline = load("build_utils/jenkins_lib/pipeJavaLib.groovy")
    }

    def buildImageTag = "269686d735abef363f9f40a1bf4e1b7c751f3722"
    javaLibPipeline(buildImageTag)
}