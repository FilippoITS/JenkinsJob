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
                // Recupera il token per usarlo nello script Shell Maven
                MY_SONAR_TOKEN = credentials('SonarQubeToken') 
            }
            steps {
                script {
                    // Lancia l'analisi
                    withSonarQubeEnv('SonarServer') {
                        sh """
                            mvn -f templates/back-end/src/job/pom.xml \
                            clean verify sonar:sonar \
                            -Dsonar.projectKey=TestSonarQube \
                            -Dsonar.login=${MY_SONAR_TOKEN}
                        """
                    }
                    
                    // Aspetta il risultato (Quality Gate)
                    // Nota: Funzionerà solo se hai fatto il PASSO 1 nella config di Jenkins
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
                def containerIp = sh(script: 'hostname -i', returnStdout: true).trim()
                def gatewayIp   = containerIp.tokenize('.')[0..2].join('.') + '.1'
                
                // --- CORREZIONE QUI: Uso le chiavi snake_case (come Sonar) ---
                def metricsMap = [
                    alert_status: 'UNKNOWN',
                    bugs: '0',
                    vulnerabilities: '0',
                    code_smells: '0',              // Era codeSmells, ORA è code_smells
                    coverage: '0.0',
                    duplicated_lines_density: '0.0' // Era duplications, ORA è duplicated_lines_density
                ]

                if (currentBuild.currentResult == 'SUCCESS' || currentBuild.currentResult == 'UNSTABLE') {
                    withCredentials([string(credentialsId: 'SonarQubeToken', variable: 'SONAR_TOKEN')]) {
                        try {
                            // Queste metriche SONO l'Overall Code (nessun prefisso "new_")
                            def sonarApiUrl = "http://${gatewayIp}:9000/api/measures/component?component=TestSonarQube&metricKeys=bugs,vulnerabilities,code_smells,coverage,duplicated_lines_density,alert_status"
                            
                            def sonarResponse = sh(script: "curl -s -f -u ${SONAR_TOKEN}: ${sonarApiUrl}", returnStdout: true).trim()
                            def jsonSlurper = new JsonSlurper()
                            def sonarData = jsonSlurper.parseText(sonarResponse)
                            
                            if (sonarData?.component?.measures) {
                                sonarData.component.measures.each { measure ->
                                    // Sovrascrive i default con i dati veri
                                    metricsMap[measure.metric] = measure.value
                                }
                            }
                        } catch (Exception e) {
                            echo "Errore lettura metriche: ${e.message}"
                        }
                    }
                }

                def apiUrl = "http://${gatewayIp}:8090/api/webhooks/jenkins"
                def gitUrl = scm ? scm.getUserRemoteConfigs()[0].getUrl() : "https://github.com/placeholder"
                def buildStatus = (currentBuild.currentResult == 'SUCCESS') ? 'true' : 'false'

                // Costruiamo il JSON finale usando le chiavi corrette dalla mappa
                def payload = JsonOutput.toJson([
                    repoUrl: gitUrl,
                    qualityGate: buildStatus,
                    sonarStats: [
                        status: metricsMap['alert_status'],
                        bugs: metricsMap['bugs'],
                        vulnerabilities: metricsMap['vulnerabilities'],
                        codeSmells: metricsMap['code_smells'],              // Ora trova la chiave corretta
                        coverage: metricsMap['coverage'],
                        duplications: metricsMap['duplicated_lines_density'] // Ora trova la chiave corretta
                    ]
                ])

                echo "Invio dati a WebApp..."
                try {
                     sh "curl -v -X POST -H 'Content-Type: application/json' -d '${payload}' ${apiUrl}"
                } catch (Exception e) {
                     echo "Errore invio Webhook: ${e.message}"
                }
            }
        }
    }
}