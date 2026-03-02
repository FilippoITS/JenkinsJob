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
                MY_SONAR_TOKEN = credentials('SONAR_API_TOKEN') 
            }
            steps {
                script {
                    withSonarQubeEnv('SonarQubeServer') {
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
                def sonarBaseUrl = "https://sonarqube.ci.dev.adamantic.cloud" 
                def backendBaseUrl = "https://adm-ci.ci.dev.adamantic.cloud" 
                
                def metricsMap = [
                    bugs: '0',
                    vulnerabilities: '0',
                    code_smells: '0',
                    coverage: '0.0',
                    duplicated_lines_density: '0.0',
                    security_hotspots: '0', // <-- AGGIUNTO QUI (Default)
                    ncloc: '0' 
                ]

                if (currentBuild.currentResult == 'SUCCESS' || currentBuild.currentResult == 'UNSTABLE') {
                    withCredentials([string(credentialsId: 'SONAR_API_TOKEN', variable: 'SonarQubeToken')]) {
                        try {
                            // <-- AGGIUNTO 'security_hotspots' NELLA STRINGA QUI SOTTO
                            def metricKeys = "bugs,vulnerabilities,code_smells,coverage,duplicated_lines_density,ncloc,security_hotspots"
                            def sonarApiUrl = "${sonarBaseUrl}/api/measures/component?component=TestSonarQube&metricKeys=${metricKeys}"
                            
                            echo "=== DEBUG API SONARQUBE ==="
                            echo "Chiamata URL: ${sonarApiUrl}"
                            
                            def sonarResponse = sh(script: "curl -s -u \$SonarQubeToken: '${sonarApiUrl}'", returnStdout: true).trim()
                            
                            echo "Risposta da SonarQube: ${sonarResponse}"
                            echo "==========================="
                            
                            def jsonSlurper = new JsonSlurper()
                            def sonarData = jsonSlurper.parseText(sonarResponse)
                            
                            if (sonarData?.component?.measures) {
                                sonarData.component.measures.each { measure ->
                                    metricsMap[measure.metric] = measure.value
                                }
                                echo "Metriche aggiornate con successo!"
                            } else {
                                echo "Attenzione: Nessuna metrica trovata nel JSON!"
                            }
                        } catch (Exception e) {
                            echo "Warning: Impossibile recuperare metriche SonarQube: ${e.message}"
                        }
                    }
                }

                def apiUrl = "${backendBaseUrl}/api/webhooks/jenkins"
                def gitUrl = scm ? scm.getUserRemoteConfigs()[0].getUrl() : "UNKNOWN_REPO"
                def buildStatus = (currentBuild.currentResult == 'SUCCESS') ? 'true' : 'false'

                def payload = JsonOutput.toJson([
                    repoUrl: ,
                    qualityGate: buildStatus,
                    sonarStats: [
                        bugs: metricsMap['bugs'],
                        vulnerabilities: metricsMap['vulnerabilities'],
                        codeSmells: metricsMap['code_smells'],
                        coverage: metricsMap['coverage'],
                        duplications: metricsMap['duplicated_lines_density'],
                        securityHotspots: metricsMap['security_hotspots'], // <-- AGGIUNTO QUI NEL PAYLOAD
                        ncloc: metricsMap['ncloc']
                    ]
                ])

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