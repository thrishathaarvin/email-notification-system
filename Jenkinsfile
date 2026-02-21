pipeline {
    agent any

    environment {
        // Backend
        JAVA_HOME = tool name: 'JDK25', type: 'jdk'        // Your Jenkins JDK installation
        NODEJS_HOME = tool name: 'Node18', type: 'nodejs'  // Your Jenkins NodeJS installation
        PATH = "${JAVA_HOME}/bin:${NODEJS_HOME}/bin:${env.PATH}"

        // Secrets (from Jenkins credentials)
        SENDGRID_API_KEY = credentials('SENDGRID_API_KEY')
    }

    stages {

        // -----------------------
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/thrishathaarvin/email-notification-system.git'
            }
        }

        // -----------------------
        stage('Backend Build & Test') {
            steps {
                dir('backend') {
                    script {
                        // Use Maven installed in Jenkins
                        def mvnHome = tool name: 'Maven3', type: 'maven'
                        sh "${mvnHome}/bin/mvn clean package"
                        sh "${mvnHome}/bin/mvn test"
                    }
                }
            }
            post {
                always {
                    junit 'backend/target/surefire-reports/*.xml'
                }
            }
        }

        // -----------------------
        stage('Frontend Build') {
            steps {
                dir('frontend') {
                    sh 'npm install'
                    sh 'npm run build'
                }
            }
        }

        // -----------------------
        stage('Run Playwright E2E Tests') {
            steps {
                dir('frontend') {
                    // Headed mode to watch the browser
                    sh 'npx playwright test --headed'
                }
            }
            post {
                always {
                    archiveArtifacts artifacts: 'frontend/playwright-report/**', allowEmptyArchive: true
                }
            }
        }

        // -----------------------
        stage('Mock Deployment') {
            steps {
                echo "Deploying backend & frontend to mock deployment folder..."
                sh 'rm -rf deploy && mkdir deploy'
                sh 'cp -r backend/target deploy/backend'
                sh 'cp -r frontend/build deploy/frontend'
                echo "Deployment done!"
            }
        }
    }

    post {
        success {
            echo "Pipeline finished successfully ✅"
        }
        failure {
            echo "Pipeline failed ❌"
        }
    }
}