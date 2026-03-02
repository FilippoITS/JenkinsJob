pipeline {
    agent any
    
    environment {
        SONAR_PROJECT_KEY = "TestSonarQube"
        MY_SONAR_TOKEN = credentials('SONAR_API_TOKEN') 
    }
    
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
            steps {
                script {
                    withSonarQubeEnv('SonarQubeServer') {
                        // Aggiunto il parametro esplicito per JaCoCo XML Report
                        sh """
                            mvn -f templates/back-end/src/job/pom.xml \
                            clean verify sonar:sonar \
                            -Dsonar.projectKey=${env.SONAR_PROJECT_KEY} \
                            -Dsonar.login=${MY_SONAR_TOKEN} \
                            -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                        """
                    }
                    
                    timeout(time: 5, unit: 'MINUTES') {
                        // Impostato a false: la pipeline non fallirà se il Quality Gate non è superato
                        waitForQualityGate abortPipeline: false
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
                    security_hotspots: '0', 
                    ncloc: '0' 
                ]

                if (currentBuild.currentResult == 'SUCCESS' || currentBuild.currentResult == 'UNSTABLE') {
                    withCredentials([string(credentialsId: 'SONAR_API_TOKEN', variable: 'SonarQubeToken')]) {
                        try {
                            def metricKeys = "bugs,vulnerabilities,code_smells,coverage,duplicated_lines_density,ncloc,security_hotspots"
                            
                            def sonarApiUrl = "${sonarBaseUrl}/api/measures/component?component=${env.SONAR_PROJECT_KEY}&metricKeys=${metricKeys}"
                            
                            echo "=== DEBUG API SONARQUBE ==="
                            echo "Chiamata URL: ${sonarApiUrl}"
                            
                            def sonarResponse = sh(script: "curl -s -u \$SonarQubeToken: '${sonarApiUrl}'", returnStdout: true).trim()
                            
                            // Usiamo la classe completa invece dell'import
                            def jsonSlurper = new groovy.json.JsonSlurper()
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
                // Se la build è SUCCESS o UNSTABLE (Quality gate fallito ma non bloccante), inviamo true o false in base alle tue preferenze.
                // Qui mappiamo SUCCESS come true, tutto il resto come false.
                def buildStatus = (currentBuild.currentResult == 'SUCCESS') ? 'true' : 'false'

                // Usiamo la classe completa invece dell'import
                def payload = groovy.json.JsonOutput.toJson([
                    repoUrl: gitUrl,
                    qualityGate: buildStatus,
                    sonarJob: env.SONAR_PROJECT_KEY,  
                    sonarStats: [
                        bugs: metricsMap['bugs'],
                        vulnerabilities: metricsMap['vulnerabilities'],
                        codeSmells: metricsMap['code_smells'],
                        coverage: metricsMap['coverage'],
                        duplications: metricsMap['duplicated_lines_density'],
                        securityHotspots: metricsMap['security_hotspots'], 
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