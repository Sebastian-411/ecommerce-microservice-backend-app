#!/bin/bash

# Script para validar que la construcción fue exitosa

set -e

PROJECT_VERSION=${PROJECT_VERSION:-0.1.0}
SERVICES=(
    "service-discovery"
    "cloud-config"
    "api-gateway"
    "proxy-client"
    "user-service"
    "product-service"
    "favourite-service"
    "order-service"
    "payment-service"
    "shipping-service"
)

echo "=========================================="
echo "Validando Construcción"
echo "=========================================="

ERRORS=0

# Verificar JARs
echo ""
echo "📦 Verificando artefactos JAR..."
for service in "${SERVICES[@]}"; do
    JAR_FILE="${service}/target/${service}-v${PROJECT_VERSION}.jar"
    if [ -f "$JAR_FILE" ]; then
        SIZE=$(du -h "$JAR_FILE" | cut -f1)
        echo "✅ ${service}: ${JAR_FILE} (${SIZE})"
    else
        echo "❌ ${service}: JAR no encontrado"
        ERRORS=$((ERRORS + 1))
    fi
done

# Verificar imágenes Docker
echo ""
echo "🐳 Verificando imágenes Docker..."
for service in "${SERVICES[@]}"; do
    if docker images | grep -q "${service}-ecommerce-boot"; then
        echo "✅ ${service}: Imagen Docker encontrada"
    else
        echo "❌ ${service}: Imagen Docker no encontrada"
        ERRORS=$((ERRORS + 1))
    fi
done

# Resumen
echo ""
echo "=========================================="
if [ $ERRORS -eq 0 ]; then
    echo "✅ Validación exitosa: Todos los artefactos están presentes"
    exit 0
else
    echo "❌ Validación falló: ${ERRORS} error(es) encontrado(s)"
    exit 1
fi

