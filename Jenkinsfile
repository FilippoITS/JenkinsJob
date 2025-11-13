pipeline {
    agent any
    environment {
        DOCKERHUB_CRED = 'dockerhub-credentials'   // ID credenziali Docker
    }
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/FilippoITS/JenkinsJob', credentialsId: 'git-credentials'
            }
        }
        stage('List Files') {
    		steps {
        		script {
            sh 'ls -R'  // Elenca ricorsivamente tutte le cartelle e i file
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

        stage('Deploy to Kubernetes') {
            steps {
                withKubeConfig([credentialsId: 'kubeconfig-staging', kubeconfigFile: '/var/lib/jenkins/.kube/config']) {
                    sh """
                    helm upgrade --install backend ./helm_chart/backend --set image.tag=${BUILD_NUMBER}
                    helm upgrade --install frontend ./helm_chart/frontend --set image.tag=${BUILD_NUMBER}
                    helm upgrade --install postgres ./helm_chart/postgres --set image.tag=${BUILD_NUMBER}
                    """
                }
            }
        }
    }
}
