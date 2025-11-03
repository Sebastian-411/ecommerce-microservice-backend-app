#!/bin/bash

# Script para ejecutar pruebas contra servicios desplegados en Kubernetes (Staging)
# Este script puede ser llamado desde Jenkins o ejecutado manualmente

set -e

NAMESPACE=${KUBECTL_NAMESPACE:-ecommerce-stage}
API_GATEWAY_URL=${API_GATEWAY_URL:-http://localhost:8080}
TIMEOUT=${TIMEOUT:-300}

echo "=========================================="
echo "Pruebas de Servicios Desplegados"
echo "Entorno: STAGING"
echo "Namespace: ${NAMESPACE}"
echo "=========================================="

# Función para verificar si un pod está listo
wait_for_pod() {
    local service_name=$1
    local timeout=$2
    
    echo "Esperando a que ${service_name} esté listo..."
    local elapsed=0
    while [ $elapsed -lt $timeout ]; do
        local ready=$(kubectl get pods -n ${NAMESPACE} -l app=${service_name} -o jsonpath='{.items[*].status.conditions[?(@.type=="Ready")].status}' | grep -o "True" | wc -l)
        local total=$(kubectl get pods -n ${NAMESPACE} -l app=${service_name} --no-headers 2>/dev/null | wc -l)
        
        if [ "$ready" -eq "$total" ] && [ "$total" -gt 0 ]; then
            echo "✅ ${service_name} está listo"
            return 0
        fi
        
        sleep 5
        elapsed=$((elapsed + 5))
    done
    
    echo "❌ Timeout esperando ${service_name}"
    return 1
}

# Función para configurar port-forward
setup_port_forward() {
    local service_name=$1
    local local_port=$2
    local service_port=${3:-8080}
    
    echo "Configurando port-forward para ${service_name}..."
    kubectl port-forward -n ${NAMESPACE} service/${service_name} ${local_port}:${service_port} > /dev/null 2>&1 &
    local pid=$!
    sleep 3
    
    # Verificar que el port-forward funciona
    if curl -f http://localhost:${local_port}/actuator/health > /dev/null 2>&1; then
        echo "✅ Port-forward para ${service_name} configurado (PID: $pid)"
        echo $pid > /tmp/port-forward-${service_name}.pid
        return 0
    else
        echo "⚠️  Port-forward para ${service_name} no responde"
        kill $pid 2>/dev/null || true
        return 1
    fi
}

# Función para limpiar port-forwards
cleanup_port_forwards() {
    echo "Limpiando port-forwards..."
    for pid_file in /tmp/port-forward-*.pid; do
        if [ -f "$pid_file" ]; then
            local pid=$(cat $pid_file)
            kill $pid 2>/dev/null || true
            rm -f $pid_file
        fi
    done
    pkill -f "kubectl port-forward" || true
}

# Capturar señales para limpiar
trap cleanup_port_forwards EXIT INT TERM

# Verificar acceso a Kubernetes
echo "Verificando acceso a Kubernetes..."
kubectl cluster-info > /dev/null 2>&1 || {
    echo "❌ No se puede acceder al cluster de Kubernetes"
    exit 1
}

# Verificar que el namespace existe
kubectl get namespace ${NAMESPACE} > /dev/null 2>&1 || {
    echo "❌ Namespace ${NAMESPACE} no existe"
    exit 1
}

# Esperar a que los servicios críticos estén listos
echo ""
echo "Esperando a que los servicios estén listos..."
wait_for_pod "service-discovery" 120
wait_for_pod "api-gateway" 180
wait_for_pod "user-service" 120
wait_for_pod "product-service" 120

# Configurar port-forwards
echo ""
echo "Configurando port-forwards..."
setup_port_forward "api-gateway" 8080 8080 || exit 1

# Esperar un poco más para que los servicios estén completamente listos
echo ""
echo "Esperando a que los servicios estén completamente listos..."
sleep 10

# Ejecutar pruebas smoke
echo ""
echo "=========================================="
echo "Ejecutando Pruebas Smoke"
echo "=========================================="

# Test 1: Health Check API Gateway
echo "Test 1: Health Check API Gateway"
if curl -f -s http://localhost:8080/actuator/health | grep -q "UP"; then
    echo "✅ API Gateway está saludable"
else
    echo "❌ API Gateway no está saludable"
    exit 1
fi

# Test 2: Endpoint Users
echo ""
echo "Test 2: Endpoint /user-service/api/users"
response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/user-service/api/users || echo "000")
if [ "$response" = "200" ] || [ "$response" = "404" ] || [ "$response" = "401" ]; then
    echo "✅ Endpoint Users responde (HTTP $response)"
else
    echo "❌ Endpoint Users no responde correctamente (HTTP $response)"
    exit 1
fi

# Test 3: Endpoint Products
echo ""
echo "Test 3: Endpoint /product-service/api/products"
response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/product-service/api/products || echo "000")
if [ "$response" = "200" ] || [ "$response" = "404" ] || [ "$response" = "401" ]; then
    echo "✅ Endpoint Products responde (HTTP $response)"
else
    echo "❌ Endpoint Products no responde correctamente (HTTP $response)"
    exit 1
fi

# Test 4: Endpoint Orders
echo ""
echo "Test 4: Endpoint /order-service/api/orders"
response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/order-service/api/orders || echo "000")
if [ "$response" = "200" ] || [ "$response" = "404" ] || [ "$response" = "401" ]; then
    echo "✅ Endpoint Orders responde (HTTP $response)"
else
    echo "❌ Endpoint Orders no responde correctamente (HTTP $response)"
    exit 1
fi

# Ejecutar pruebas E2E si están disponibles
if [ "$RUN_E2E_TESTS" = "true" ]; then
    echo ""
    echo "=========================================="
    echo "Ejecutando Pruebas E2E"
    echo "=========================================="
    
    export SPRING_PROFILES_ACTIVE=stage
    export API_GATEWAY_URL=http://localhost:8080
    
    # Ejecutar pruebas E2E
    ./mvnw test -Dtest="*E2ETest" -Dspring.profiles.active=stage -Dapi.gateway.url=http://localhost:8080 || {
        echo "⚠️  Algunas pruebas E2E fallaron"
        exit 1
    }
fi

# Ejecutar pruebas de integración si están disponibles
if [ "$RUN_INTEGRATION_TESTS" = "true" ]; then
    echo ""
    echo "=========================================="
    echo "Ejecutando Pruebas de Integración"
    echo "=========================================="
    
    export SPRING_PROFILES_ACTIVE=stage
    export API_GATEWAY_URL=http://localhost:8080
    
    # Ejecutar pruebas de integración
    ./mvnw test -Dtest="*IntegrationTest" -Dspring.profiles.active=stage -Dapi.gateway.url=http://localhost:8080 || {
        echo "⚠️  Algunas pruebas de integración fallaron"
        exit 1
    }
fi

echo ""
echo "=========================================="
echo "✅ Todas las pruebas completadas"
echo "=========================================="

