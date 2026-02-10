import groovy.json.JsonSlurper
import groovy.json.JsonOutput

pipeline {
    agent any
    
    tools {
        maven 'maven_3.9'
    }
    
    stages {
        stage('Build') {
            steps {
                echo 'Sto compilando il progetto...'
                // sh "mvn -f templates/back-end/src/job/pom.xml clean compile"
            }
        }

        stage('Test') {
            steps {
                echo 'Esecuzione test...'
            }
        }

        stage('SonarQube Analysis') {
            environment {
                MY_SONAR_TOKEN = credentials('SonarQubeToken') 
            }
            steps {
                script {
                    withSonarQubeEnv('SonarServer') {
                        sh """
                            mvn -f templates/back-end/src/job/pom.xml \
                            clean verify sonar:sonar \
                            -Dsonar.projectKey=TestSonarQube \
                            -Dsonar.login=${MY_SONAR_TOKEN}
                        """
                    }
                    
                    // 1. ASPETTA CHE SONARQUBE FINISCA I CALCOLI
                    // Questo step mette in pausa la pipeline finché Sonar non risponde "Fatto!"
                    timeout(time: 2, unit: 'MINUTES') {
                        waitForQualityGate abortPipeline: true
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                // Recupero IP per le chiamate
                def containerIp = sh(script: 'hostname -i', returnStdout: true).trim()
                def gatewayIp   = containerIp.tokenize('.')[0..2].join('.') + '.1'
                
                // Credenziale per le chiamate API
                withCredentials([string(credentialsId: 'SonarQubeToken', variable: 'SONAR_TOKEN')]) {
                    
                    echo "--- RECUPERO METRICHE DA SONARQUBE ---"
                    
                    // 2. CHIAMATA API A SONARQUBE
                    // Chiediamo: bugs, vulnerabilità, code smells, coverage e duplicazioni
                    def sonarApiUrl = "http://${gatewayIp}:9000/api/measures/component?component=TestSonarQube&metricKeys=bugs,vulnerabilities,code_smells,coverage,duplicated_lines_density,alert_status"
                    
                    // Eseguiamo curl e salviamo la risposta JSON in una variabile
                    def sonarResponse = sh(script: "curl -s -u ${SONAR_TOKEN}: ${sonarApiUrl}", returnStdout: true).trim()
                    
                    // 3. PARSING DEI DATI (Estraiamo i numeri dal JSON di Sonar)
                    def jsonSlurper = new JsonSlurper()
                    def sonarData = jsonSlurper.parseText(sonarResponse)
                    
                    // Mappa per semplificare i dati da mandare alla tua app
                    def metricsMap = [:]
                    sonarData.component.measures.each { measure ->
                        metricsMap[measure.metric] = measure.value
                    }

                    echo "Metriche estratte: ${metricsMap}"

                    // 4. PREPARIAMO IL PAYLOAD PER LA TUA WEBAPP
                    def apiUrl      = "http://${gatewayIp}:8090/api/webhooks/jenkins"
                    def gitUrl      = scm ? scm.getUserRemoteConfigs()[0].getUrl() : "https://github.com/repo-placeholder"
                    def buildStatus = (currentBuild.currentResult == 'SUCCESS') ? 'true' : 'false'

                    def payload = JsonOutput.toJson([
                        repoUrl: gitUrl,
                        qualityGate: buildStatus,
                        sonarStats: [
                            status: metricsMap['alert_status'], // OK o ERROR
                            bugs: metricsMap['bugs'],
                            vulnerabilities: metricsMap['vulnerabilities'],
                            codeSmells: metricsMap['code_smells'],
                            coverage: metricsMap['coverage'],
                            duplications: metricsMap['duplicated_lines_density']
                        ]
                    ])

                    echo "Invio webhook a: ${apiUrl}"
                    
                    // 5. INVIO ALLA TUA WEBAPP
                    sh "curl -v -X POST -H 'Content-Type: application/json' -d '${payload}' ${apiUrl}"
                }
            }
        }
    }
}