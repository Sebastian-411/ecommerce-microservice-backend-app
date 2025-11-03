# Pipelines de Construcción para Entorno DEV

Este directorio contiene los pipelines y scripts para la construcción de microservicios en el entorno de desarrollo.

## Archivos Principales

### Jenkinsfile.dev
Pipeline principal para construcción completa en entorno DEV. Este pipeline:
- Construye todos los microservicios respetando dependencias
- Genera artefactos JAR
- Construye imágenes Docker
- Ejecuta tests
- Valida la construcción

### build-dev-pipeline.groovy
Pipeline reutilizable para construir un microservicio individual. Puede ser usado por múltiples jobs de Jenkins.

## Scripts de Construcción

### scripts/build-service.sh
Construye un microservicio individual:
```bash
./scripts/build-service.sh user-service 8700
```

### scripts/build-all-services.sh
Construye todos los microservicios en el orden correcto:
```bash
./scripts/build-all-services.sh
```

### scripts/validate-build.sh
Valida que todos los artefactos fueron generados correctamente:
```bash
./scripts/validate-build.sh
```

## Estructura de Microservicios

Los microservicios se construyen en el siguiente orden:

1. **Infraestructura** (deben construirse primero):
   - service-discovery (8761)
   - cloud-config (9296)

2. **Servicios Core**:
   - api-gateway (8080)
   - proxy-client (8900)

3. **Servicios de Negocio** (pueden construirse en paralelo):
   - user-service (8700)
   - product-service (8500)
   - favourite-service (8800)
   - order-service (8300)
   - payment-service (8400)
   - shipping-service (8600)

## Variables de Entorno

- `PROJECT_VERSION`: Versión del proyecto (default: 0.1.0)
- `DOCKER_REGISTRY`: Registry de Docker (default: selimhorri)
- `SKIP_TESTS`: Saltar tests durante la construcción (default: false)
- `JAVA_VERSION`: Versión de Java (default: 11)

## Uso en Jenkins

### Configurar Job para Jenkinsfile.dev

1. Crear un nuevo Pipeline job
2. En "Pipeline definition", seleccionar "Pipeline script from SCM"
3. SCM: Git
4. Script Path: `Jenkinsfile.dev`

### Configurar Job para Pipeline Modular

Para construir un microservicio individual usando el pipeline compartido:

```groovy
@Library('jenkins-shared-library') _

buildDevPipeline([
    serviceName: 'user-service',
    port: '8700',
    dockerRegistry: 'selimhorri',
    runTests: true
])
```

## Orden de Ejecución del Pipeline

1. **Preparación del Entorno**: Verifica herramientas
2. **Checkout**: Descarga código del repositorio
3. **Validación de Código**: Valida POM, dependencias, sintaxis
4. **Instalar Dependencias**: Instala módulos compartidos
5. **Compilación de Infraestructura**: Construye service-discovery y cloud-config
6. **Compilación de Servicios Core**: Construye api-gateway y proxy-client
7. **Compilación de Servicios de Negocio**: Construye todos los servicios de negocio
8. **Ejecución de Tests**: Ejecuta tests unitarios e integración
9. **Construcción de Artefactos**: Empaqueta todos los JARs
10. **Construcción de Imágenes Docker**: Construye imágenes Docker para cada servicio
11. **Validación de Imágenes**: Verifica que las imágenes fueron construidas
12. **Publicación Local**: Archiva artefactos localmente

## Troubleshooting

### Error: JAR no encontrado
- Verifica que el proyecto se compiló correctamente
- Verifica la versión del proyecto en pom.xml

### Error: Imagen Docker no se construye
- Verifica que Docker está corriendo
- Verifica que el JAR existe antes de construir la imagen

### Error: Dependencias faltantes
- Ejecuta `./mvnw clean install -N` primero
- Verifica que los servicios de infraestructura se construyeron antes que los demás

## Mejores Prácticas

1. Siempre construir en el orden de dependencias
2. Validar la construcción después de cada etapa
3. Ejecutar tests antes de construir imágenes Docker
4. Usar tags específicos de versión para las imágenes
5. Limpiar workspace después de cada build

