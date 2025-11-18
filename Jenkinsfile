pipeline {
    agent any

    environment {
        DOCKERHUB_CRED = 'dockerhub-credentials'   // ID credenziali Docker
        KUBECONFIG = '/var/lib/jenkins/.kube/config'
        SonarQubeToken = credentials('SonarQube-token')
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

        stage('SCM') {
            steps {
                checkout scm
            }
        }

        stage('SonarQube Analysis') {
            environment {
                scannerHome = tool 'SonarScanner'
            }
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh """
                        ${scannerHome}/bin/sonar-scanner \
                        -Dsonar.projectKey=job-app \
                        -Dsonar.sources=templates/back-end/src/job/src/main/java,templates/front-end/src/job-app/src \
                        -Dsonar.host.url=http://localhost:9000 \
                        -Dsonar.login=${SonarQubeToken}
                    """
                }
            }
        }

        stage("Quality Gate") {
            steps {
                timeout(time: 15, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

    }
}
