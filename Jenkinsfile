pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                echo 'Sto compilando il progetto...'
            }
        }
        stage('Test') {
            steps {
                echo 'Esecuzione test...'
            }
        }
    }

    post {
        success {
            script {
                // Chiama Java dicendo SUCCESS
                def payload = """{"projectId": ${params.projectId}, "status": "SUCCESS"}"""
                sh "curl -X POST -H 'Content-Type: application/json' -d '${payload}' http://host.docker.internal:8090/api/webhooks/jenkins/result"
            }
        }
        failure {
            script {
                // Chiama Java dicendo FAILURE
                def payload = """{"projectId": ${params.projectId}, "status": "FAILURE"}"""
                sh "curl -X POST -H 'Content-Type: application/json' -d '${payload}' http://host.docker.internal:8090/api/webhooks/jenkins/result"
            }
        }
    }
}