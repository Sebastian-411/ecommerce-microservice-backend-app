pipeline {
    agent any
    
    environment {
        DOCKER_REGISTRY = 'selimhorri'
        PROJECT_VERSION = '0.1.0'
        JAVA_VERSION = '11'
    }
    
    tools {
        maven 'Maven-3.8'
        jdk 'JDK-11'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                sh '''
                    echo "Building all microservices..."
                    ./mvnw clean package -DskipTests
                '''
            }
        }
        
        stage('Test') {
            parallel {
                stage('Unit Tests') {
                    steps {
                        sh './mvnw test'
                    }
                    post {
                        always {
                            junit '**/target/surefire-reports/TEST-*.xml'
                        }
                    }
                }
            }
        }
        
        stage('Docker Build') {
            parallel {
                stage('Build Service Discovery') {
                    steps {
                        sh '''
                            docker build -t ${DOCKER_REGISTRY}/service-discovery-ecommerce-boot:${PROJECT_VERSION} \
                                -f service-discovery/Dockerfile .
                        '''
                    }
                }
                stage('Build Cloud Config') {
                    steps {
                        sh '''
                            docker build -t ${DOCKER_REGISTRY}/cloud-config-ecommerce-boot:${PROJECT_VERSION} \
                                -f cloud-config/Dockerfile .
                        '''
                    }
                }
                stage('Build API Gateway') {
                    steps {
                        sh '''
                            docker build -t ${DOCKER_REGISTRY}/api-gateway-ecommerce-boot:${PROJECT_VERSION} \
                                -f api-gateway/Dockerfile .
                        '''
                    }
                }
                stage('Build Proxy Client') {
                    steps {
                        sh '''
                            docker build -t ${DOCKER_REGISTRY}/proxy-client-ecommerce-boot:${PROJECT_VERSION} \
                                -f proxy-client/Dockerfile .
                        '''
                    }
                }
                stage('Build User Service') {
                    steps {
                        sh '''
                            docker build -t ${DOCKER_REGISTRY}/user-service-ecommerce-boot:${PROJECT_VERSION} \
                                -f user-service/Dockerfile .
                        '''
                    }
                }
                stage('Build Product Service') {
                    steps {
                        sh '''
                            docker build -t ${DOCKER_REGISTRY}/product-service-ecommerce-boot:${PROJECT_VERSION} \
                                -f product-service/Dockerfile .
                        '''
                    }
                }
                stage('Build Favourite Service') {
                    steps {
                        sh '''
                            docker build -t ${DOCKER_REGISTRY}/favourite-service-ecommerce-boot:${PROJECT_VERSION} \
                                -f favourite-service/Dockerfile .
                        '''
                    }
                }
                stage('Build Order Service') {
                    steps {
                        sh '''
                            docker build -t ${DOCKER_REGISTRY}/order-service-ecommerce-boot:${PROJECT_VERSION} \
                                -f order-service/Dockerfile .
                        '''
                    }
                }
                stage('Build Shipping Service') {
                    steps {
                        sh '''
                            docker build -t ${DOCKER_REGISTRY}/shipping-service-ecommerce-boot:${PROJECT_VERSION} \
                                -f shipping-service/Dockerfile .
                        '''
                    }
                }
                stage('Build Payment Service') {
                    steps {
                        sh '''
                            docker build -t ${DOCKER_REGISTRY}/payment-service-ecommerce-boot:${PROJECT_VERSION} \
                                -f payment-service/Dockerfile .
                        '''
                    }
                }
            }
        }
        
        stage('Docker Push') {
            when {
                anyOf {
                    branch 'master'
                    branch 'stage'
                    branch 'develop'
                }
            }
            steps {
                script {
                    def services = [
                        'service-discovery',
                        'cloud-config',
                        'api-gateway',
                        'proxy-client',
                        'user-service',
                        'product-service',
                        'favourite-service',
                        'order-service',
                        'shipping-service',
                        'payment-service'
                    ]
                    
                    withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                        
                        for (service in services) {
                            sh """
                                docker push ${DOCKER_REGISTRY}/${service}-ecommerce-boot:${PROJECT_VERSION}
                                docker tag ${DOCKER_REGISTRY}/${service}-ecommerce-boot:${PROJECT_VERSION} \
                                    ${DOCKER_REGISTRY}/${service}-ecommerce-boot:latest
                                docker push ${DOCKER_REGISTRY}/${service}-ecommerce-boot:latest
                            """
                        }
                    }
                }
            }
        }
        
        stage('Deploy to Kubernetes') {
            when {
                anyOf {
                    branch 'master'
                    branch 'stage'
                }
            }
            steps {
                script {
                    def env = env.BRANCH_NAME == 'master' ? 'prod' : 'stage'
                    sh """
                        echo "Deploying to ${env} environment..."
                        kubectl apply -f k8s/${env}/ --recursive
                        kubectl rollout status deployment/service-discovery -n ecommerce-${env}
                        kubectl rollout status deployment/cloud-config -n ecommerce-${env}
                        kubectl rollout status deployment/api-gateway -n ecommerce-${env}
                    """
                }
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
        success {
            echo 'Pipeline executed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
            mail(
                to: 'admin@example.com',
                subject: "Pipeline Failed: ${env.JOB_NAME} - ${env.BUILD_NUMBER}",
                body: "Check console output at ${env.BUILD_URL}"
            )
        }
    }
}

