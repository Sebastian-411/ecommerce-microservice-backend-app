#!/bin/bash

# Script para desplegar los microservicios en el entorno de producción

set -e

echo "Desplegando microservicios en Kubernetes (Producción)..."

# Crear namespace si no existe
kubectl create namespace ecommerce-prod --dry-run=client -o yaml | kubectl apply -f -

# Aplicar configuraciones de Kubernetes usando kustomize
kubectl apply -k k8s/prod/

echo "Esperando a que los pods estén listos..."
kubectl wait --for=condition=ready pod --all -n ecommerce-prod --timeout=300s

echo "Despliegue completado!"
echo "Servicios desplegados:"
kubectl get services -n ecommerce-prod
echo ""
echo "Pods desplegados:"
kubectl get pods -n ecommerce-prod

