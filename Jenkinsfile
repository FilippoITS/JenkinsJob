pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                echo 'Sto compilando il progetto...'
            }
        }
        stage('Test') {
            steps {
                echo 'Esecuzione test...'
            }
        }
    }

    post {
    always {
        script {
           def gatewayIp = sh(script: "/sbin/ip route | awk '/default/ { print $3 }'", returnStdout: true).trim()
            
            echo "IP del Gateway rilevato: ${gatewayIp}"

             def apiUrl = "http://${gatewayIp}:8090/api/webhooks/jenkins/result"
            
            def gitUrl = scm.getUserRemoteConfigs()[0].getUrl()
            def buildStatus = currentBuild.currentResult == 'SUCCESS' ? 'true' : 'false'

            def payload = groovy.json.JsonOutput.toJson([
                repoUrl: gitUrl,
                qualityGate: buildStatus
            ])

            echo "Invio a: ${apiUrl}"
            
            sh "curl -v -X POST -H 'Content-Type: application/json' -d '${payload}' ${apiUrl}"
        }
    }
}
}