/**
 * Pipeline compartido para construcción de microservicios en DEV
 * Este pipeline puede ser reutilizado por múltiples jobs de Jenkins
 */

def call(Map config) {
    pipeline {
        agent any
        
        environment {
            SERVICE_NAME = "${config.serviceName}"
            SERVICE_PORT = "${config.port}"
            ENVIRONMENT = 'dev'
            DOCKER_REGISTRY = "${config.dockerRegistry ?: 'selimhorri'}"
            PROJECT_VERSION = "${env.BUILD_NUMBER}"
            JAVA_VERSION = "${config.javaVersion ?: '11'}"
        }
        
        tools {
            maven "${config.mavenVersion ?: 'Maven-3.8'}"
            jdk "JDK-${JAVA_VERSION}"
        }
        
        options {
            timeout(time: 30, unit: 'MINUTES')
            timestamps()
        }
        
        stages {
            stage('Checkout') {
                steps {
                    checkout scm
                }
            }
            
            stage('Build') {
                steps {
                    script {
                        echo "Construyendo ${SERVICE_NAME}..."
                        sh """
                            ./mvnw clean package -pl ${SERVICE_NAME} -am -DskipTests -q
                            
                            if [ ! -f "${SERVICE_NAME}/target/${SERVICE_NAME}-v0.1.0.jar" ]; then
                                echo "❌ JAR no encontrado después de la compilación"
                                exit 1
                            fi
                            
                            echo "✅ ${SERVICE_NAME} compilado exitosamente"
                            ls -lh ${SERVICE_NAME}/target/*.jar
                        """
                    }
                }
            }
            
            stage('Test') {
                when {
                    expression { config.runTests != false }
                }
                steps {
                    sh "./mvnw test -pl ${SERVICE_NAME}"
                }
                post {
                    always {
                        junit "${SERVICE_NAME}/**/target/surefire-reports/TEST-*.xml"
                    }
                }
            }
            
            stage('Docker Build') {
                steps {
                    script {
                        def imageTag = "${DOCKER_REGISTRY}/${SERVICE_NAME}-ecommerce-boot:${PROJECT_VERSION}"
                        sh """
                            docker build -t ${imageTag} \\
                                --build-arg PROJECT_VERSION=0.1.0 \\
                                -f ${SERVICE_NAME}/Dockerfile .
                            
                            docker tag ${imageTag} ${DOCKER_REGISTRY}/${SERVICE_NAME}-ecommerce-boot:latest
                            
                            echo "✅ Imagen Docker construida: ${imageTag}"
                            docker images | grep ${SERVICE_NAME}-ecommerce-boot
                        """
                    }
                }
            }
            
            stage('Archive') {
                steps {
                    archiveArtifacts artifacts: "${SERVICE_NAME}/target/*.jar", allowEmptyArchive: false
                }
            }
        }
        
        post {
            always {
                cleanWs()
            }
            success {
                echo "✅ Pipeline completado exitosamente para ${SERVICE_NAME}"
            }
            failure {
                echo "❌ Pipeline falló para ${SERVICE_NAME}"
            }
        }
    }
}

