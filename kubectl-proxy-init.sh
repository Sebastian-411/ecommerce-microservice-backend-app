#!/bin/bash
set -e

# Corregir rutas de Windows en el kubeconfig si existe
if [ -f /root/.kube/config ]; then
  echo "Corrigiendo rutas de Windows en kubeconfig..."
  
  # Crear backup
  cp /root/.kube/config /root/.kube/config.backup || true
  
  # Convertir todas las barras invertidas a barras normales primero
  sed -i 's|\\|/|g' /root/.kube/config || true
  
  # Corregir rutas específicas de Windows (múltiples patrones)
  sed -i 's|C:/Users/[^/]*/.minikube|/root/.minikube|g' /root/.kube/config || true
  sed -i 's|C:.*\.minikube|/root/.minikube|g' /root/.kube/config || true
  
  # Corregir rutas relativas incorrectas como /root/.kube/..\.minikube o /root/.kube/../.minikube
  sed -i 's|/root/.kube/\.\./\.minikube|/root/.minikube|g' /root/.kube/config || true
  sed -i 's|/root/.kube/\.\.\.minikube|/root/.minikube|g' /root/.kube/config || true
  
  # Asegurar que todas las rutas de minikube apunten a /root/.minikube (patrones más específicos)
  sed -i 's|client-certificate:.*minikube.*client\.crt|client-certificate: /root/.minikube/profiles/minikube/client.crt|g' /root/.kube/config || true
  sed -i 's|client-key:.*minikube.*client\.key|client-key: /root/.minikube/profiles/minikube/client.key|g' /root/.kube/config || true
  sed -i 's|certificate-authority:.*minikube.*ca\.crt|certificate-authority: /root/.minikube/ca.crt|g' /root/.kube/config || true
  
  # Corregir la URL del servidor para usar host.docker.internal en Windows
  # Esto permite que el contenedor acceda al host Windows donde está corriendo minikube
  sed -i 's|server: https://127\.0\.0\.1:|server: https://host.docker.internal:|g' /root/.kube/config || true
  sed -i 's|server: https://localhost:|server: https://host.docker.internal:|g' /root/.kube/config || true
  
  echo "Rutas corregidas"
  echo "Verificando rutas de certificados:"
  grep -E "(client-cert|client-key|certificate-authority)" /root/.kube/config | head -3 || true
  echo "Verificando URL del servidor:"
  grep -E "server:" /root/.kube/config || true
fi

# Ejecutar kubectl proxy
echo "Iniciando kubectl proxy..."
exec kubectl proxy --address=0.0.0.0 --accept-hosts='.*' --port=8001 --kubeconfig=/root/.kube/config --insecure-skip-tls-verify

