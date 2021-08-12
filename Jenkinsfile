#!groovy
build('db-common-lib', 'docker-host') {
    checkoutRepo()
    loadBuildUtils()

    def javaLibPipeline
    runStage('load JavaLib pipeline') {
        javaLibPipeline = load("build_utils/jenkins_lib/pipeJavaLib.groovy")
    }

    def buildImageTag = "917afcdd0c0a07bf4155d597bbba72e962e1a34a"
    javaLibPipeline(buildImageTag)
}
