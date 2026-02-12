import groovy.json.JsonSlurper
import groovy.json.JsonOutput

pipeline {
    agent any
    
    tools {
        maven 'maven_3.9'
    }
    
    stages {
        stage('Build & Test') {
            steps {
                echo 'Avvio compilazione e test...'
                // sh "mvn -f templates/back-end/src/job/pom.xml clean verify"
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
                    
                    timeout(time: 5, unit: 'MINUTES') {
                        waitForQualityGate abortPipeline: true
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                // 1. Configurazione Networking
                def containerIp = sh(script: 'hostname -i', returnStdout: true).trim()
                def gatewayIp   = containerIp.tokenize('.')[0..2].join('.') + '.1'
                
                // --- MODIFICA 1: Aggiunto ncloc ai default ---
                def metricsMap = [
                    alert_status: 'UNKNOWN',
                    bugs: '0',
                    vulnerabilities: '0',
                    code_smells: '0',
                    coverage: '0.0',
                    duplicated_lines_density: '0.0',
                    ncloc: '0' 
                ]

                // 2. Recupero metriche
                if (currentBuild.currentResult == 'SUCCESS' || currentBuild.currentResult == 'UNSTABLE') {
                    withCredentials([string(credentialsId: 'SonarQubeToken', variable: 'SONAR_TOKEN')]) {
                        try {
                            // --- MODIFICA 2: Aggiunto ncloc alla query API ---
                            def metricKeys = "bugs,vulnerabilities,code_smells,coverage,duplicated_lines_density,alert_status,ncloc"
                            
                            def sonarApiUrl = "http://${gatewayIp}:9000/api/measures/component?component=TestSonarQube&metricKeys=${metricKeys}"
                            
                            def sonarResponse = sh(script: "curl -s -f -u ${SONAR_TOKEN}: \"${sonarApiUrl}\"", returnStdout: true).trim()
                            
                            def jsonSlurper = new JsonSlurper()
                            def sonarData = jsonSlurper.parseText(sonarResponse)
                            
                            if (sonarData?.component?.measures) {
                                sonarData.component.measures.each { measure ->
                                    metricsMap[measure.metric] = measure.value
                                }
                            }
                        } catch (Exception e) {
                            echo "Warning: Impossibile recuperare metriche SonarQube: ${e.message}"
                        }
                    }
                }

                // 3. Preparazione Payload
                def apiUrl = "http://${gatewayIp}:8090/api/webhooks/jenkins"
                def gitUrl = scm ? scm.getUserRemoteConfigs()[0].getUrl() : "UNKNOWN_REPO"
                def buildStatus = (currentBuild.currentResult == 'SUCCESS') ? 'true' : 'false'

                def payload = JsonOutput.toJson([
                    repoUrl: gitUrl,
                    qualityGate: buildStatus,
                    sonarStats: [
                        status: metricsMap['alert_status'],
                        bugs: metricsMap['bugs'],
                        vulnerabilities: metricsMap['vulnerabilities'],
                        codeSmells: metricsMap['code_smells'],
                        coverage: metricsMap['coverage'],
                        duplications: metricsMap['duplicated_lines_density'],
                        // --- MODIFICA 3: Mappatura nel JSON ---
                        ncloc: metricsMap['ncloc']
                    ]
                ])

                // 4. Invio Webhook
                try {
                     sh "curl -s -X POST -H 'Content-Type: application/json' -d '${payload}' ${apiUrl}"
                     echo "Webhook inviato con successo a ${apiUrl}"
                } catch (Exception e) {
                     echo "Errore invio Webhook: ${e.message}"
                }
            }
        }
    }
}