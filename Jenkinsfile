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
                // 1. Estrazione URL Git (con gestione null-safe)
                def gitUrl = scm.getUserRemoteConfigs()[0].getUrl()
                                
                // 2. Status: currentBuild.result può essere null se la build è ancora in corso
                // Usiamo una logica più robusta per determinare il successo
                def buildStatus = currentBuild.currentResult == 'SUCCESS' ? 'true' : 'false'
                
                // 3. Configurazione Endpoint (IP del Gateway Docker standard)
                def apiUrl = "http://host.docker.internal:8090/api/webhooks/jenkins/result"
                
                echo "Invio webhook per Repo: ${gitUrl} - Status: ${buildStatus}"
                
                // Creazione JSON
                def payload = groovy.json.JsonOutput.toJson([
                    repoUrl: gitUrl,
                    qualityGate: buildStatus
                ])

                // Nota l'uso di \ davanti alle virgolette del payload per l'invio via SH
                sh "curl -X POST -H 'Content-Type: application/json' -d '${payload}' ${apiUrl}"
            }
        }
    }
}