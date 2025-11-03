#!/bin/bash

# Script para construir todos los microservicios en el orden correcto
# Respetando las dependencias entre servicios

set -e

PROJECT_VERSION=${PROJECT_VERSION:-0.1.0}
DOCKER_REGISTRY=${DOCKER_REGISTRY:-selimhorri}
SKIP_TESTS=${SKIP_TESTS:-true}

echo "=========================================="
echo "Construcción Completa de Microservicios"
echo "Entorno: DEV"
echo "Versión: ${PROJECT_VERSION}"
echo "=========================================="

# Definir servicios por grupos de dependencia
declare -a INFRASTRUCTURE_SERVICES=(
    "service-discovery:8761"
    "cloud-config:9296"
)

declare -a CORE_SERVICES=(
    "api-gateway:8080"
    "proxy-client:8900"
)

declare -a BUSINESS_SERVICES=(
    "user-service:8700"
    "product-service:8500"
    "favourite-service:8800"
    "order-service:8300"
    "payment-service:8400"
    "shipping-service:8600"
)

# Función para construir un servicio
build_service() {
    local service_info=$1
    local service_name=$(echo $service_info | cut -d':' -f1)
    local port=$(echo $service_info | cut -d':' -f2)
    
    echo ""
    echo "=========================================="
    echo "Construyendo: ${service_name}"
    echo "=========================================="
    
    ./scripts/build-service.sh "$service_name" "$port" || {
        echo "❌ Error construyendo ${service_name}"
        exit 1
    }
}

# Paso 1: Instalar dependencias del proyecto
echo ""
echo "📦 Instalando dependencias del proyecto padre..."
./mvnw clean install -N -q

# Paso 2: Construir servicios de infraestructura
echo ""
echo "🏗️  Construyendo servicios de infraestructura..."
for service in "${INFRASTRUCTURE_SERVICES[@]}"; do
    build_service "$service"
done

# Paso 3: Construir servicios core
echo ""
echo "🔧 Construyendo servicios core..."
for service in "${CORE_SERVICES[@]}"; do
    build_service "$service"
done

# Paso 4: Construir servicios de negocio (en paralelo si es posible)
echo ""
echo "💼 Construyendo servicios de negocio..."
for service in "${BUSINESS_SERVICES[@]}"; do
    build_service "$service"
done

# Resumen final
echo ""
echo "=========================================="
echo "✅ Construcción Completa Finalizada"
echo "=========================================="
echo ""
echo "Artefactos generados:"
find . -name "*.jar" -path "*/target/*" -not -path "*/.m2/*" -exec ls -lh {} \;

echo ""
echo "Imágenes Docker construidas:"
docker images | grep ecommerce-boot | head -15

echo ""
echo "=========================================="

