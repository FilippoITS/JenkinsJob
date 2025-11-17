pipeline {
    agent any

    tools {
        sonarScanner 'SonarScanner'  // Assicurati che il nome corrisponda a quello configurato in Jenkins
    }

    environment {
        DOCKERHUB_CRED = 'dockerhub-credentials'   // ID credenziali Docker
        KUBECONFIG = '/var/lib/jenkins/.kube/config'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/FilippoITS/JenkinsJob', credentialsId: 'git-credentials'
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
                scannerHome = tool 'SonarScanner'  // Usa il tool configurato nel Jenkinsfile
            }
            steps {
                withSonarQubeEnv('SonarQube') {
                    withCredentials([string(credentialsId: 'SonarQube-token', variable: 'SONAR_TOKEN')]) {
                        sh """
                            ${scannerHome}/bin/sonar-scanner \
                            -Dsonar.projectKey=myproject \
                            -Dsonar.sources=src \
                            -Dsonar.host.url=http://localhost:9000 \
                            -Dsonar.login=${SONAR_TOKEN}
                        """
                    }
                }
            }
        }

        stage("Quality Gate") {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
    }
}
