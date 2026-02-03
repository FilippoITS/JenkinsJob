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
                        // 1. Recupera l'IP del container (funziona ovunque)
                        def containerIp = sh(script: "hostname -i", returnStdout: true).trim()
                        
                        // 2. Calcola il Gateway usando Groovy (più sicuro della bash)
                        // Prende "172.17.0.5", toglie l'ultimo numero e mette "1" -> "172.17.0.1"
                        def gatewayIp = containerIp.tokenize('.')[0..2].join('.') + '.1'
                        
                        echo "Container IP: ${containerIp}"
                        echo "Gateway IP calcolato: ${gatewayIp}"

                        def apiUrl = "http://${gatewayIp}:8090/api/webhooks/jenkins/result"
                        def gitUrl = scm.getUserRemoteConfigs()[0].getUrl()
                        def buildStatus = currentBuild.currentResult == 'SUCCESS' ? 'true' : 'false'

                        def payload = groovy.json.JsonOutput.toJson([
                            repoUrl: gitUrl,
                            qualityGate: buildStatus
                        ])

                        echo "Invio webhook a: ${apiUrl}"
                        
                        // 3. Esegue la chiamata
                        sh "curl -v -X POST -H 'Content-Type: application/json' -d '${payload}' ${apiUrl}"
                    }
    }
    }
}