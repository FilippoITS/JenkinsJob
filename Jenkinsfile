pipeline {
    agent any

    environment {
        DOCKERHUB_CRED = 'dockerhub-credentials'
        KUBECONFIG = '/var/lib/jenkins/.kube/config'
        SonarQubeToken = credentials('SonarQube-token')
        JAVA_HOME = '/opt/jdk-17'
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
    }


    stages {

        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/FilippoITS/JenkinsJob', credentialsId: 'git-credentials'
            }
        }

        stage('Test Frontend JS') {
            agent {
                docker {
                    image 'node:18-alpine'
                    // Riutilizza il workspace corrente così i file restano per Sonar
                    reuseNode true
                }
            }
            steps {
                dir('templates/front-end/src/job-app') {
                    // Installa dipendenze ed esegue i test dentro il container Node
                    sh 'npm install'
                    sh 'npm test -- --coverage --watchAll=false'

                }
            }
        }

        stage('Build Backend Java') {
            steps {
                dir('templates/back-end/src/job') {
                    sh 'mvn clean verify'
                }
            }
        }

        stage('Build Backend Docker') {
            steps {
                script {
                    docker.build("adamanticfilippo/backend:${env.BUILD_NUMBER}", "-f templates/back-end/src/job/Dockerfile .")
                }
            }
        }

        stage('Build Frontend Docker') {
            steps {
                script {
                    docker.build("adamanticfilippo/frontend:${env.BUILD_NUMBER}", "-f templates/front-end/src/job-app/Dockerfile .")
                }
            }
        }

        stage('Build Postgres Docker') {
            steps {
                script {
                    docker.build("adamanticfilippo/postgres:${env.BUILD_NUMBER}", "-f templates/database/Dockerfile .")
                }
            }
        }

        stage('Push Docker Images') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', DOCKERHUB_CRED) {
                        docker.image("adamanticfilippo/backend:${env.BUILD_NUMBER}").push()
                        docker.image("adamanticfilippo/frontend:${env.BUILD_NUMBER}").push()
                        docker.image("adamanticfilippo/postgres:${env.BUILD_NUMBER}").push()
                    }
                }
            }
        }

        stage('Add hosts entry') {
            steps {
                sh '''
                INGRESS_IP=$(kubectl get svc ingress-nginx-controller -n ingress-nginx -o jsonpath='{.status.loadBalancer.ingress[0].ip}')
                echo "$INGRESS_IP jobexample.adamantic.net" | sudo tee -a /etc/hosts
                '''
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                sh """
                helm upgrade --install job-app . \
                  --namespace default --create-namespace \
                  --set backend.image.tag=${BUILD_NUMBER} \
                  --set frontend.image.tag=${BUILD_NUMBER} \
                  --set database.image.tag=${BUILD_NUMBER}
                """
            }
        }

        stage('SonarQube Analysis') {
            environment {
                scannerHome = tool 'SonarScanner'
            }
            steps {
                withSonarQubeEnv('SonarQube') {
                    // Fix per i percorsi del frontend
                    sh """
                        sed -i 's|SF:src/|SF:templates/front-end/src/job-app/src/|g' templates/front-end/src/job-app/coverage/lcov.info
                    """

                    // Debug per verificare che il file esista e sia corretto
                    sh "echo '--- DEBUG LCOV CONTENT ---'"
                    sh "head -n 5 templates/front-end/src/job-app/coverage/lcov.info || echo 'FILE NON TROVATO'"
                    sh "echo '--------------------------'"

                    // Scanner Command
                    sh """
                        ${scannerHome}/bin/sonar-scanner \
                        -Dsonar.projectKey=job-app \
                        \
                        -Dsonar.sources=templates/back-end/src/job/pom.xml,templates/back-end/src/job/src/main/java,templates/front-end/src/job-app/src \
                        -Dsonar.exclusions=**/*.test.js,**/*.spec.js,**/setupTests.js,**/reportWebVitals.js,**/index.js \
                        \
                        -Dsonar.tests=templates/back-end/src/job/src/test/java,templates/front-end/src/job-app/src \
                        -Dsonar.test.inclusions=**/*.test.js,**/*.spec.js \
                        \
                        -Dsonar.java.binaries=templates/back-end/src/job/target/classes \
                        -Dsonar.coverage.jacoco.xmlReportPaths=templates/back-end/src/job/target/site/jacoco/jacoco.xml \
                        -Dsonar.javascript.lcov.reportPaths=templates/front-end/src/job-app/coverage/lcov.info \
                        \
                        -Dsonar.host.url=http://localhost:9000 \
                        -Dsonar.token=${SonarQubeToken}
                    """
                }
            }
        }
    }
}