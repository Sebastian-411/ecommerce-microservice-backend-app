#!/bin/bash

# Script para iniciar Jenkins localmente

set -e

echo "=========================================="
echo "Iniciando Jenkins Local"
echo "=========================================="

# Verificar que Docker está ejecutándose
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker no está ejecutándose. Por favor, inicia Docker primero."
    exit 1
fi

echo "✅ Docker está ejecutándose"

# Verificar que docker-compose está disponible
if ! command -v docker-compose > /dev/null 2>&1; then
    echo "❌ docker-compose no está instalado"
    exit 1
fi

echo "✅ docker-compose está disponible"

# Iniciar Jenkins
echo "Iniciando Jenkins con docker-compose..."
docker-compose -f docker-compose-jenkins.yml up -d

echo "Esperando a que Jenkins esté listo..."
sleep 15

# Obtener la contraseña inicial
echo "Obteniendo contraseña inicial de Jenkins..."
JENKINS_PASSWORD=$(docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword 2>/dev/null || echo "")

if [ ! -z "$JENKINS_PASSWORD" ]; then
    echo ""
    echo "=========================================="
    echo "✅ Jenkins está listo!"
    echo "=========================================="
    echo ""
    echo "URL: http://localhost:8080"
    echo "Contraseña inicial: ${JENKINS_PASSWORD}"
    echo ""
    echo "Pasos siguientes:"
    echo "1. Accede a http://localhost:8080"
    echo "2. Ingresa la contraseña inicial"
    echo "3. Instala los plugins sugeridos"
    echo "4. Crea un usuario administrador"
    echo "5. Configura un nuevo Pipeline Job apuntando a Jenkinsfile.dev"
    echo ""
else
    echo "⚠️  Jenkins está iniciando. Por favor, espera unos minutos y accede a http://localhost:8080"
    echo "Para obtener la contraseña inicial, ejecuta:"
    echo "  docker exec jenkins-local cat /var/jenkins_home/secrets/initialAdminPassword"
fi

echo ""
echo "Para detener Jenkins, ejecuta:"
echo "  docker-compose -f docker-compose-jenkins-local.yml down"

