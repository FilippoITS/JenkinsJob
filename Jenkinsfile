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
                
                // Valori di default (tutti a zero o UNKNOWN)
                def metricsMap = [
                    status: 'UNKNOWN',
                    bugs: '0',
                    vulnerabilities: '0',
                    codeSmells: '0',
                    coverage: '0.0',
                    duplications: '0.0'
                ]

                // Recuperiamo le metriche SOLO se la build non è fallita prima (Compile/Test)
                if (currentBuild.currentResult == 'SUCCESS' || currentBuild.currentResult == 'UNSTABLE') {
                    withCredentials([string(credentialsId: 'SonarQubeToken', variable: 'SONAR_TOKEN')]) {
                        try {
                            echo "--- RECUPERO METRICHE OVERALL DA SONARQUBE ---"
                            // Le chiavi senza prefisso 'new_' indicano metriche OVERALL
                            def sonarApiUrl = "http://${gatewayIp}:9000/api/measures/component?component=TestSonarQube&metricKeys=bugs,vulnerabilities,code_smells,coverage,duplicated_lines_density,alert_status"
                            
                            def sonarResponse = sh(script: "curl -s -f -u ${SONAR_TOKEN}: ${sonarApiUrl}", returnStdout: true).trim()
                            
                            def jsonSlurper = new JsonSlurper()
                            def sonarData = jsonSlurper.parseText(sonarResponse)
                            
                            if (sonarData?.component?.measures) {
                                sonarData.component.measures.each { measure ->
                                    metricsMap[measure.metric] = measure.value
                                }
                            }
                        } catch (Exception e) {
                            echo "Non sono riuscito a leggere le metriche (SonarQube spento o errore rete): ${e.message}"
                        }
                    }
                }

                // Invio Webhook
                def apiUrl      = "http://${gatewayIp}:8090/api/webhooks/jenkins"
                // Se scm è null (es. test locale), usa un placeholder
                def gitUrl      = scm ? scm.getUserRemoteConfigs()[0].getUrl() : "https://local-test"
                def buildStatus = (currentBuild.currentResult == 'SUCCESS') ? 'true' : 'false'

                def payload = JsonOutput.toJson([
                    repoUrl: gitUrl,
                    qualityGate: buildStatus,
                    sonarStats: [
                        status: metricsMap['alert_status'] ?: 'UNKNOWN',
                        bugs: metricsMap['bugs'],
                        vulnerabilities: metricsMap['vulnerabilities'],
                        codeSmells: metricsMap['code_smells'],
                        coverage: metricsMap['coverage'],
                        duplications: metricsMap['duplicated_lines_density']
                    ]
                ])

                echo "Invio dati Overall a WebApp: ${apiUrl}"
                try {
                     sh "curl -v -X POST -H 'Content-Type: application/json' -d '${payload}' ${apiUrl}"
                } catch (Exception e) {
                     echo "Errore invio Webhook: ${e.message}"
                }
            }
        }
    }
}