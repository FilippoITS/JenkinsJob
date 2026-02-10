pipeline {
    agent any
    
    tools {
        maven 'maven_3.9'
    }
    
    stages {
        stage('Build') {
            steps {
                echo 'Sto compilando il progetto...'
                // Qui andrebbe sh "mvn -f templates/back-end/src/job/pom.xml clean compile"
            }
        }

        stage('Test') {
            steps {
                echo 'Esecuzione test...'
            }
        }

        stage('SonarQube Analysis') {
            // 1. Aggiungiamo questo blocco per caricare il secret
            environment {
                // 'SonarQubeToken' è l'ID che hai dato su Jenkins
                MY_SONAR_TOKEN = credentials('SonarQubeToken') 
            }
            steps {
                withSonarQubeEnv('SonarServer') {
                    // 2. Usiamo la variabile ${MY_SONAR_TOKEN} nel comando
                    sh """
                        mvn -f templates/back-end/src/job/pom.xml \
                        clean verify sonar:sonar \
                        -Dsonar.projectKey=TestSonarQube \
                        -Dsonar.token=${MY_SONAR_TOKEN}
                        """
                }
            }
        }
    }

    post {
        always {
            script {
                def containerIp = sh(script: 'hostname -i', returnStdout: true).trim()
                // Nota: questo calcolo presuppone una rete docker standard /16
                def gatewayIp   = containerIp.tokenize('.')[0..2].join('.') + '.1'

                echo "Container IP: ${containerIp}"
                echo "Gateway IP calcolato: ${gatewayIp}"

                def apiUrl      = "http://${gatewayIp}:8090/api/webhooks/jenkins"
                // Controllo difensivo se scm è null (può capitare in test locali)
                def gitUrl      = scm ? scm.getUserRemoteConfigs()[0].getUrl() : "https://github.com/repo-placeholder"
                def buildStatus = (currentBuild.currentResult == 'SUCCESS') ? 'true' : 'false'

                def payload = groovy.json.JsonOutput.toJson([
                    repoUrl: gitUrl,
                    qualityGate: buildStatus
                ])

                echo "Invio webhook a: ${apiUrl}"

                // Invio effettivo
                sh "curl -v -X POST -H 'Content-Type: application/json' -d '${payload}' ${apiUrl}"
            }
        }
    }
}