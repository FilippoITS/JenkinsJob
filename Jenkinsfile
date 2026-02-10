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
                
                // 1. Definiamo i valori di default (Mock)
                def metricsMap = [
                    alert_status: 'MOCK_DATA', // Se vedi questo nel DB, la chiamata API è fallita
                    bugs: '-1',
                    vulnerabilities: '-1',
                    code_smells: '-1',
                    coverage: '-1.0',
                    duplicated_lines_density: '-1.0'
                ]

                // Eseguiamo solo se la compilazione è andata bene
                if (currentBuild.currentResult == 'SUCCESS' || currentBuild.currentResult == 'UNSTABLE') {
                    withCredentials([string(credentialsId: 'SonarQubeToken', variable: 'SONAR_TOKEN')]) {
                        try {
                            echo "================= INIZIO DEBUG API SONARQUBE ================="
                            
                            // Definisco le chiavi che voglio (Overall)
                            def keys = "bugs,vulnerabilities,code_smells,coverage,duplicated_lines_density,alert_status"
                            
                            // Definisco l'URL
                            def sonarApiUrl = "http://${gatewayIp}:9000/api/measures/component?component=TestSonarQube&metricKeys=${keys}"
                            echo "1. URL Chiamata: ${sonarApiUrl}"
                            
                            // Eseguo CURL senza flag silenziosi per vedere eventuali errori di connessione
                            // Tolgo il -f per vedere il corpo anche in caso di errore 404/401
                            def sonarResponse = sh(script: "curl -v -u ${SONAR_TOKEN}: \"${sonarApiUrl}\"", returnStdout: true).trim()
                            
                            echo "2. RISPOSTA GREZZA DA SONARQUBE:"
                            echo "${sonarResponse}"
                            
                            // Parsing
                            def jsonSlurper = new JsonSlurper()
                            def sonarData = jsonSlurper.parseText(sonarResponse)
                            
                            // Verifica esistenza dati
                            if (sonarData?.component?.measures) {
                                echo "3. Trovate ${sonarData.component.measures.size()} metriche. Inizio mappatura:"
                                sonarData.component.measures.each { measure ->
                                    echo "   - Metrica trovata: [${measure.metric}] Valore: [${measure.value}]"
                                    
                                    // Aggiorno la mappa
                                    metricsMap[measure.metric] = measure.value
                                }
                            } else {
                                echo "ERROR: Il JSON è valido ma non contiene 'component.measures'. Forse il Project Key 'TestSonarQube' è errato?"
                                if (sonarData?.errors) {
                                    echo "SONAR ERROR: ${sonarData.errors}"
                                }
                            }
                            echo "================= FINE DEBUG API SONARQUBE ================="

                        } catch (Exception e) {
                            echo "EXCEPTION GRAVE DURANTE IL PARSING: ${e.message}"
                        }
                    }
                }

                // Invio al Backend
                def apiUrl = "http://${gatewayIp}:8090/api/webhooks/jenkins"
                def gitUrl = scm ? scm.getUserRemoteConfigs()[0].getUrl() : "https://github.com/placeholder"
                def buildStatus = (currentBuild.currentResult == 'SUCCESS') ? 'true' : 'false'

                def payload = JsonOutput.toJson([
                    repoUrl: gitUrl,
                    qualityGate: buildStatus,
                    sonarStats: [
                        status: metricsMap['alert_status'],
                        bugs: metricsMap['bugs'],
                        vulnerabilities: metricsMap['vulnerabilities'],
                        codeSmells: metricsMap['code_smells'], // Nota: Chiave camelCase per il tuo backend, valore preso da chiave snake_case della mappa
                        coverage: metricsMap['coverage'],
                        duplications: metricsMap['duplicated_lines_density']
                    ]
                ])

                echo "Invio payload finale: ${payload}"
                try {
                     sh "curl -v -X POST -H 'Content-Type: application/json' -d '${payload}' ${apiUrl}"
                } catch (Exception e) {
                     echo "Errore invio Webhook: ${e.message}"
                }
            }
        }
    }sadda
}