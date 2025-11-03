#!/bin/bash

# Script para configurar Jenkins con los plugins necesarios

set -e

echo "Iniciando Jenkins..."

# Iniciar Jenkins usando docker-compose
docker-compose -f docker-compose-jenkins.yml up -d

echo "Esperando a que Jenkins esté listo..."
sleep 30

# Obtener la contraseña inicial de Jenkins
JENKINS_PASSWORD=$(docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword 2>/dev/null || echo "")

if [ ! -z "$JENKINS_PASSWORD" ]; then
    echo "Jenkins está listo!"
    echo "Contraseña inicial de Jenkins: ${JENKINS_PASSWORD}"
    echo "Accede a Jenkins en: http://localhost:8080"
else
    echo "Jenkins está iniciando. Por favor, espera unos minutos y accede a http://localhost:8080"
fi

echo ""
echo "Para configurar Jenkins manualmente:"
echo "1. Accede a http://localhost:8080"
echo "2. Instala los plugins sugeridos"
echo "3. Configura las credenciales de Docker Hub"
echo "4. Configura la conexión con Kubernetes"
echo ""
echo "Plugins recomendados:"
echo "  - Kubernetes Plugin"
echo "  - Docker Pipeline Plugin"
echo "  - Docker Plugin"
echo "  - Kubernetes CLI Plugin"
echo "  - Pipeline: Stage View Plugin"

