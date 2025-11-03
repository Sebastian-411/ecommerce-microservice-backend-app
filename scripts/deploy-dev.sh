#!/bin/bash

# Script para desplegar los microservicios en el entorno de desarrollo

set -e

echo "Desplegando microservicios en Kubernetes (Desarrollo)..."

# Crear namespace si no existe
kubectl create namespace ecommerce-dev --dry-run=client -o yaml | kubectl apply -f -

# Aplicar configuraciones de Kubernetes
kubectl apply -f k8s/dev/

echo "Esperando a que los pods estén listos..."
kubectl wait --for=condition=ready pod --all -n ecommerce-dev --timeout=300s

echo "Despliegue completado!"
echo "Servicios desplegados:"
kubectl get services -n ecommerce-dev
echo ""
echo "Pods desplegados:"
kubectl get pods -n ecommerce-dev

