pipeline {
    agent any
    
    tools {
        maven 'maven_3.9'
    }
    
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
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarServer') {
                    sh "mvn -f templates/back-end/src/job/pom.xml sonar:sonar -Dsonar.projectKey=TestSonarQube"
                }
            }
        }
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarServer') {
                    sh "mvn -f templates/back-end/src/job/pom.xml sonar:sonar " +
                    "-Dsonar.projectKey=TestSonarQube " +
                    "-Dsonar.token= sqp_e1d7574b297cd646de8f3c4db98e6d3045273d40" 
                    }
                }
            }
    }

    post {
        always {
            script {
                def containerIp = sh(script: 'hostname -i', returnStdout: true).trim()

                def gatewayIp = containerIp.tokenize('.')[0..2].join('.') + '.1'

                echo "Container IP: ${containerIp}"
                echo "Gateway IP calcolato: ${gatewayIp}"

                def apiUrl = "http://${gatewayIp}:8090/api/webhooks/jenkins"
                def gitUrl = scm.getUserRemoteConfigs()[0].getUrl()
                def buildStatus = currentBuild.currentResult == 'SUCCESS' ? 'true' : 'false'

                def payload = groovy.json.JsonOutput.toJson([
                    repoUrl: gitUrl,
                    qualityGate: buildStatus
                ])

                echo "Invio webhook a: ${apiUrl}"

                sh "curl -v -X POST -H 'Content-Type: application/json' -d '${payload}' ${apiUrl}"
            }
        }
    }
}
