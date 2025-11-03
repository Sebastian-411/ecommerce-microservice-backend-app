#!/bin/bash

# Script para construir todas las imágenes Docker de los microservicios

set -e

PROJECT_VERSION=${PROJECT_VERSION:-0.1.0}
DOCKER_REGISTRY=${DOCKER_REGISTRY:-selimhorri}

echo "Construyendo imágenes Docker para versión ${PROJECT_VERSION}..."

# Construir el proyecto primero
echo "Compilando proyecto..."
./mvnw clean package -DskipTests

# Construir imágenes Docker para cada servicio
SERVICES=(
  "service-discovery"
  "cloud-config"
  "api-gateway"
  "proxy-client"
  "user-service"
  "product-service"
  "favourite-service"
  "order-service"
  "shipping-service"
  "payment-service"
)

for service in "${SERVICES[@]}"; do
  echo "Construyendo imagen para ${service}..."
  docker build -t ${DOCKER_REGISTRY}/${service}-ecommerce-boot:${PROJECT_VERSION} \
    -f ${service}/Dockerfile .
  docker tag ${DOCKER_REGISTRY}/${service}-ecommerce-boot:${PROJECT_VERSION} \
    ${DOCKER_REGISTRY}/${service}-ecommerce-boot:latest
done

echo "¡Todas las imágenes Docker han sido construidas exitosamente!"
docker images | grep ecommerce-boot

