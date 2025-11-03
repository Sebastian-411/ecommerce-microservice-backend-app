#!/bin/bash

# Script para construir un microservicio individual
# Uso: ./build-service.sh <service-name> <port>

set -e

SERVICE_NAME=${1}
PORT=${2}
PROJECT_VERSION=${PROJECT_VERSION:-0.1.0}
DOCKER_REGISTRY=${DOCKER_REGISTRY:-selimhorri}

if [ -z "$SERVICE_NAME" ]; then
    echo "❌ Error: Se requiere el nombre del servicio"
    echo "Uso: ./build-service.sh <service-name> <port>"
    echo "Ejemplo: ./build-service.sh user-service 8700"
    exit 1
fi

if [ -z "$PORT" ]; then
    echo "❌ Error: Se requiere el puerto del servicio"
    exit 1
fi

echo "=========================================="
echo "Construyendo Microservicio: ${SERVICE_NAME}"
echo "=========================================="
echo "Puerto: ${PORT}"
echo "Versión: ${PROJECT_VERSION}"
echo "=========================================="

# Verificar que el servicio existe
if [ ! -d "$SERVICE_NAME" ]; then
    echo "❌ Error: El directorio ${SERVICE_NAME} no existe"
    exit 1
fi

# Paso 1: Instalar dependencias del proyecto padre
echo ""
echo "📦 Instalando dependencias del proyecto padre..."
./mvnw clean install -N -q || echo "⚠️ Advertencia: Error al instalar dependencias padre"

# Paso 2: Compilar el microservicio
echo ""
echo "🔨 Compilando ${SERVICE_NAME}..."
./mvnw clean compile -pl ${SERVICE_NAME} -am -q

if [ $? -ne 0 ]; then
    echo "❌ Error al compilar ${SERVICE_NAME}"
    exit 1
fi

# Paso 3: Empaquetar el microservicio
echo ""
echo "📦 Empaquetando ${SERVICE_NAME}..."
./mvnw package -pl ${SERVICE_NAME} -DskipTests -q

if [ $? -ne 0 ]; then
    echo "❌ Error al empaquetar ${SERVICE_NAME}"
    exit 1
fi

# Verificar que el JAR se generó
JAR_FILE="${SERVICE_NAME}/target/${SERVICE_NAME}-v${PROJECT_VERSION}.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo "❌ Error: JAR no encontrado: ${JAR_FILE}"
    exit 1
fi

echo "✅ JAR generado: ${JAR_FILE}"
ls -lh "$JAR_FILE"

# Paso 4: Construir imagen Docker
echo ""
echo "🐳 Construyendo imagen Docker para ${SERVICE_NAME}..."
IMAGE_NAME="${DOCKER_REGISTRY}/${SERVICE_NAME}-ecommerce-boot:${PROJECT_VERSION}"
docker build -t "${IMAGE_NAME}" \
    --build-arg PROJECT_VERSION=${PROJECT_VERSION} \
    -f "${SERVICE_NAME}/Dockerfile" .

if [ $? -ne 0 ]; then
    echo "❌ Error al construir imagen Docker"
    exit 1
fi

# Tag como latest
docker tag "${IMAGE_NAME}" "${DOCKER_REGISTRY}/${SERVICE_NAME}-ecommerce-boot:latest"

echo "✅ Imagen Docker construida: ${IMAGE_NAME}"
docker images | grep "${SERVICE_NAME}-ecommerce-boot"

echo ""
echo "=========================================="
echo "✅ Construcción completada exitosamente"
echo "=========================================="
echo "Servicio: ${SERVICE_NAME}"
echo "JAR: ${JAR_FILE}"
echo "Imagen Docker: ${IMAGE_NAME}"
echo "=========================================="

