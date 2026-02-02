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
                // 1. Estrazione Automatica dell'URL Git
                // scm.getUserRemoteConfigs() prende la configurazione Git del job corrente
                def gitUrl = scm.getUserRemoteConfigs()[0].getUrl()
                                
                // 2. Status
                def isSuccess = (currentBuild.result == 'SUCCESS') ? 'true' : 'false'
                
                // 3. Invio al Backend
                def apiUrl = "http://host.docker.internal:8080/api/webhooks/jenkins/result"
                
                echo "Invio webhook per Repo: ${gitUrl}"
                
                // Usiamo groovy.json.JsonOutput per creare il JSON sicuro (gestisce escape caratteri)
                def payload = groovy.json.JsonOutput.toJson([
                    repoUrl: gitUrl,
                    qualityGate: isSuccess
                ])

                sh "curl -X POST -H 'Content-Type: application/json' -d '${payload}' ${apiUrl}"
            }
        }
    }
}