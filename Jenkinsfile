node {
    stage('check out') {
        checkout scm
        sh 'git submodule update --init'
    }
    stage('build') {
        sh 'chmod +x gradlew'
        sh './gradlew clean assemble --stacktrace'
    }
    stage('sign apk') {
        sh '$ANDROID_BUILD_TOOLS/zipalign -v -p 4 app/build/outputs/apk/release/app-*-unsigned.apk app/build/outputs/apk/app-unsigned-aligned.apk'
        withCredentials([string(credentialsId: 'signingPassSecret', variable: 'JKS_PSS')]) {
            sh '$ANDROID_BUILD_TOOLS/apksigner sign --ks /var/lib/jenkins/PlumboardKeyAndroid.jks --ks-pass pass:$JKS_PSS --out signed.apk app/build/outputs/apk/app-unsigned-aligned.apk'
        }
    }
    stage('ATTACH APK') {
        archiveArtifacts 'signed.apk'
    }

    stage('PUBLISH') {
        androidApkUpload apkFilesPattern: 'signed.apk', googleCredentialsId: 'plumboard', trackName: 'internal'
    }
}