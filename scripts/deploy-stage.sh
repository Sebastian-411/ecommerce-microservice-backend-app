#!/bin/bash

# Script para desplegar los microservicios en el entorno de staging

set -e

echo "Desplegando microservicios en Kubernetes (Staging)..."

# Crear namespace si no existe
kubectl create namespace ecommerce-stage --dry-run=client -o yaml | kubectl apply -f -

# Aplicar configuraciones de Kubernetes usando kustomize
kubectl apply -k k8s/stage/

echo "Esperando a que los pods estén listos..."
kubectl wait --for=condition=ready pod --all -n ecommerce-stage --timeout=300s

echo "Despliegue completado!"
echo "Servicios desplegados:"
kubectl get services -n ecommerce-stage
echo ""
echo "Pods desplegados:"
kubectl get pods -n ecommerce-stage

