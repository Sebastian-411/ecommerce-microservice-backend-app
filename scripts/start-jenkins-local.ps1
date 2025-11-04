# Script PowerShell para iniciar Jenkins localmente en Windows

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Iniciando Jenkins Local" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

# Verificar que Docker está ejecutándose
try {
    docker info | Out-Null
    Write-Host "✅ Docker está ejecutándose" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker no está ejecutándose. Por favor, inicia Docker Desktop primero." -ForegroundColor Red
    exit 1
}

# Verificar que docker-compose está disponible
try {
    docker-compose --version | Out-Null
    Write-Host "✅ docker-compose está disponible" -ForegroundColor Green
} catch {
    Write-Host "❌ docker-compose no está instalado" -ForegroundColor Red
    exit 1
}

# Iniciar Jenkins
Write-Host "Iniciando Jenkins con docker-compose..." -ForegroundColor Yellow
docker-compose -f docker-compose-jenkins.yml up -d

Write-Host "Esperando a que Jenkins esté listo..." -ForegroundColor Yellow
Start-Sleep -Seconds 15

# Obtener la contraseña inicial
Write-Host "Obteniendo contraseña inicial de Jenkins..." -ForegroundColor Yellow
$JENKINS_PASSWORD = docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword 2>$null

if ($JENKINS_PASSWORD) {
    Write-Host ""
    Write-Host "==========================================" -ForegroundColor Green
    Write-Host "✅ Jenkins está listo!" -ForegroundColor Green
    Write-Host "==========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "URL: http://localhost:8080" -ForegroundColor Cyan
    Write-Host "Contraseña inicial: $JENKINS_PASSWORD" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Pasos siguientes:" -ForegroundColor Cyan
    Write-Host "1. Accede a http://localhost:8080"
    Write-Host "2. Ingresa la contraseña inicial"
    Write-Host "3. Instala los plugins sugeridos"
    Write-Host "4. Crea un usuario administrador"
    Write-Host "5. Configura un nuevo Pipeline Job apuntando a Jenkinsfile.dev"
    Write-Host ""
} else {
    Write-Host "⚠️  Jenkins está iniciando. Por favor, espera unos minutos y accede a http://localhost:8080" -ForegroundColor Yellow
    Write-Host "Para obtener la contraseña inicial, ejecuta:" -ForegroundColor Yellow
    Write-Host "  docker exec jenkins-local cat /var/jenkins_home/secrets/initialAdminPassword" -ForegroundColor Gray
}

Write-Host ""
Write-Host "Para detener Jenkins, ejecuta:" -ForegroundColor Cyan
Write-Host "  docker-compose -f docker-compose-jenkins-local.yml down" -ForegroundColor Gray

