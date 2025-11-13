pipeline {
    agent any

    environment {
        REGISTRY = "adamanticfilippo"
        BUILD_NUMBER = "${env.BUILD_NUMBER}"
        KUBE_NAMESPACE = "default"
    }

    stages {
        stage('Build Docker images') {
            steps {
                script {
                    docker.build("${REGISTRY}/backend:${BUILD_NUMBER}", "./backend")
                    docker.build("${REGISTRY}/frontend:${BUILD_NUMBER}", "./frontend")
                    docker.build("${REGISTRY}/postgres:${BUILD_NUMBER}", "./postgres")
                }
            }
        }

        stage('Push Docker images') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'docker-credentials') {
                        docker.image("${REGISTRY}/backend:${BUILD_NUMBER}").push()
                        docker.image("${REGISTRY}/frontend:${BUILD_NUMBER}").push()
                        docker.image("${REGISTRY}/postgres:${BUILD_NUMBER}").push()
                    }
                }
            }
        }

        stage('Install ingress-nginx (if missing)') {
            steps {
                script {
                    def ingressCheck = sh(script: "kubectl get ns ingress-nginx --ignore-not-found", returnStdout: true).trim()
                    if (!ingressCheck) {
                        echo "Ingress controller not found. Installing..."
                        sh "kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.14.1/deploy/static/provider/cloud/deploy.yaml"
                        sh "kubectl wait --namespace ingress-nginx --for=condition=ready pod --all --timeout=180s"
                    } else {
                        echo "Ingress controller already installed."
                    }
                }
            }
        }

        stage('Deploy application') {
            steps {
                script {
                    // Applica i tuoi manifest o Helm chart
                    sh "helm upgrade --install job-app ./helm-chart --set backend.tag=${BUILD_NUMBER},frontend.tag=${BUILD_NUMBER},database.tag=${BUILD_NUMBER}"
                }
            }
        }

        stage('Verify deployment') {
            steps {
                script {
                    sh "kubectl get pods"
                    sh "kubectl get svc"
                    sh "kubectl get ingress"
                }
            }
        }
    }
}
