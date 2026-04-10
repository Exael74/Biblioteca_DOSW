# ⚙️ Guía DevOps — Cobertura y Despliegue

Proyecto: **DOSW-ParcialT2**

---

## 📌 Tabla de Contenidos

1. [JaCoCo — Cobertura de Código](#1-jacoco--cobertura-de-código)
2. [SonarQube — Análisis de Calidad](#2-sonarqube--análisis-de-calidad)
   - [Opción A: SonarCloud (nube)](#opción-a-sonarcloud-nube---recomendada)
   - [Opción B: SonarQube local (Docker)](#opción-b-sonarqube-local-docker)
3. [Despliegue en Azure](#3-despliegue-en-azure)
   - [Azure App Service (JAR directo)](#opción-a-azure-app-service-jar-directo)
   - [Azure Container Registry + Docker](#opción-b-azure-container-registry--docker)
4. [Pipeline CI/CD completo (GitHub Actions)](#4-pipeline-cicd-completo-github-actions)

---

## 1. JaCoCo — Cobertura de Código

JaCoCo ya está configurado en el `pom.xml`. No requiere instalación adicional.

### Generar el reporte

```bash
# Ejecuta los tests y genera el reporte de cobertura
mvn clean test
```

El reporte HTML se genera automáticamente en:
```
target/site/jacoco/index.html
```
Ábrelo en el navegador para ver la cobertura por clase, método y línea.

### Verificar cobertura mínima (opcional)

Puedes forzar que el build falle si la cobertura baja de un umbral. Agrega una regla al plugin en `pom.xml`:

```xml
<execution>
    <id>check</id>
    <goals><goal>check</goal></goals>
    <configuration>
        <rules>
            <rule>
                <element>BUNDLE</element>
                <limits>
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.70</minimum> <!-- 70% mínimo -->
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</execution>
```

### Comandos útiles

| Comando | Descripción |
|---------|-------------|
| `mvn test` | Ejecuta tests y genera reporte JaCoCo |
| `mvn jacoco:report` | Regenera el reporte sin correr los tests |
| `mvn jacoco:check` | Valida los umbrales de cobertura definidos |

---

## 2. SonarQube — Análisis de Calidad

### Opción A: SonarCloud (nube) — Recomendada

**Paso 1 — Crear cuenta y proyecto:**
1. Ve a https://sonarcloud.io y regístrate con tu cuenta de GitHub
2. Haz clic en **"+"** → "Analyze new project"
3. Selecciona el repositorio `DOSW_ParcialT2_StivenPardo`
4. Elige **"Manually"** para configurar con Maven
5. Copia el **Organization Key** y el **Project Key** que te muestra

**Paso 2 — Generar token:**
1. Ve a https://sonarcloud.io/account/security
2. Haz clic en **"Generate Token"**, ponle un nombre (ej: `parcial-token`)
3. Copia el token generado (solo se muestra una vez)

**Paso 3 — Actualizar `pom.xml`:**

Reemplaza los valores en la sección `<properties>`:
```xml
<properties>
    ...
    <sonar.organization>TU_ORGANIZATION_KEY</sonar.organization>
    <sonar.host.url>https://sonarcloud.io</sonar.host.url>
    <sonar.projectKey>TU_PROJECT_KEY</sonar.projectKey>
</properties>
```

**Paso 4 — Ejecutar el análisis:**

```bash
mvn clean verify sonar:sonar \
  -Dsonar.token=TU_TOKEN_AQUI
```

> En Windows (PowerShell):
> ```powershell
> mvn clean verify sonar:sonar "-Dsonar.token=TU_TOKEN_AQUI"
> ```

Al terminar verás en la consola una URL como:
```
ANALYSIS SUCCESSFUL, you can find the results at:
https://sonarcloud.io/dashboard?id=TU_PROJECT_KEY
```

---

### Opción B: SonarQube local (Docker)

**Paso 1 — Levantar SonarQube con Docker:**

```bash
docker run --name sonarqube-local \
  -p 9000:9000 \
  -d sonarqube:lts-community
```

Espera ~1 minuto y accede a: http://localhost:9000  
Credenciales iniciales: `admin` / `admin` (te pedirá cambiarla)

**Paso 2 — Crear proyecto en la UI:**
1. Haz clic en **"Create Project"** → "Manually"
2. Ponle el nombre `DOSW-ParcialT2` y la clave `DOSW-ParcialT2`
3. En "How do you want to analyze your repository?" selecciona **"Locally"**
4. Genera un token y cópialo

**Paso 3 — Ejecutar el análisis:**

```bash
mvn clean verify sonar:sonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=TU_TOKEN_LOCAL
```

**Paso 4 — Ver resultados:**

Entra a http://localhost:9000/dashboard?id=DOSW-ParcialT2

### Comando combinado (tests + JaCoCo + Sonar)

```bash
mvn clean verify sonar:sonar -Dsonar.token=TU_TOKEN
```

> `verify` ejecuta los tests **y** genera el reporte de JaCoCo antes de enviarlo a Sonar, lo que permite ver la cobertura integrada en el dashboard.

---

## 3. Despliegue en Azure

### Pre-requisitos

1. Cuenta en Azure: https://portal.azure.com
2. Azure CLI instalado: https://learn.microsoft.com/cli/azure/install-azure-cli
3. Iniciar sesión:

```bash
az login
```

---

### Opción A: Azure App Service (JAR directo)

**Paso 1 — Empaquetar la aplicación:**

```bash
mvn clean package -DskipTests
```

El JAR se genera en `target/DOSW-ParcialT2-0.0.1-SNAPSHOT.jar`

**Paso 2 — Crear el recurso en Azure:**

```bash
# Variables (ajusta los valores)
RESOURCE_GROUP="rg-dosw-parcial"
APP_NAME="dosw-parcial-t2-app"
LOCATION="eastus"

# Crear grupo de recursos
az group create --name $RESOURCE_GROUP --location $LOCATION

# Crear plan de App Service (F1 = gratis)
az appservice plan create \
  --name plan-dosw-parcial \
  --resource-group $RESOURCE_GROUP \
  --sku F1 \
  --is-linux

# Crear la Web App con Java 17
az webapp create \
  --name $APP_NAME \
  --resource-group $RESOURCE_GROUP \
  --plan plan-dosw-parcial \
  --runtime "JAVA:17-java17"
```

**Paso 3 — Configurar variables de entorno en Azure:**

```bash
az webapp config appsettings set \
  --name $APP_NAME \
  --resource-group $RESOURCE_GROUP \
  --settings \
    POSTGRES_URL="jdbc:postgresql://tu-host:5432/parcial_db" \
    POSTGRES_USER="myuser" \
    POSTGRES_PASSWORD="tu_contraseña" \
    MONGO_URI="mongodb+srv://user:pass@cluster.mongodb.net/parcial_mongo_db" \
    JWT_SECRET="tu_jwt_secret"
```

**Paso 4 — Desplegar el JAR:**

```bash
az webapp deploy \
  --name $APP_NAME \
  --resource-group $RESOURCE_GROUP \
  --src-path target/DOSW-ParcialT2-0.0.1-SNAPSHOT.jar \
  --type jar
```

La app estará disponible en:
```
https://dosw-parcial-t2-app.azurewebsites.net
```

---

### Opción B: Azure Container Registry + Docker

**Paso 1 — Crear `Dockerfile` en la raíz del proyecto:**

```dockerfile
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/DOSW-ParcialT2-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Paso 2 — Crear Azure Container Registry (ACR):**

```bash
ACR_NAME="doswacrparcial"

az acr create \
  --name $ACR_NAME \
  --resource-group $RESOURCE_GROUP \
  --sku Basic \
  --admin-enabled true
```

**Paso 3 — Construir y subir la imagen:**

```bash
# Build del JAR primero
mvn clean package -DskipTests

# Build y push de la imagen directamente en ACR
az acr build \
  --registry $ACR_NAME \
  --image dosw-parcial-t2:latest \
  .
```

**Paso 4 — Crear Web App desde el contenedor:**

```bash
# Obtener credenciales del ACR
ACR_SERVER=$(az acr show --name $ACR_NAME --query loginServer -o tsv)
ACR_USER=$(az acr credential show --name $ACR_NAME --query username -o tsv)
ACR_PASS=$(az acr credential show --name $ACR_NAME --query passwords[0].value -o tsv)

# Crear la Web App desde la imagen Docker
az webapp create \
  --name $APP_NAME \
  --resource-group $RESOURCE_GROUP \
  --plan plan-dosw-parcial \
  --deployment-container-image-name "$ACR_SERVER/dosw-parcial-t2:latest"

# Configurar credenciales del registry
az webapp config container set \
  --name $APP_NAME \
  --resource-group $RESOURCE_GROUP \
  --docker-registry-server-url "https://$ACR_SERVER" \
  --docker-registry-server-user $ACR_USER \
  --docker-registry-server-password $ACR_PASS
```

---

## 4. Pipeline CI/CD completo (GitHub Actions)

Crea el archivo `.github/workflows/ci-cd.yml` en tu repositorio:

```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [ develop, main ]
  pull_request:
    branches: [ main ]

jobs:
  build-and-analyze:
    name: Build, Test & SonarCloud
    runs-on: ubuntu-latest

    steps:
      - name: Checkout código
        uses: actions/checkout@v4
        with:
          fetch-depth: 0  # requerido por SonarCloud

      - name: Configurar Java 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven

      - name: Build y tests con JaCoCo
        run: mvn clean verify
        env:
          POSTGRES_URL: ${{ secrets.POSTGRES_URL }}
          POSTGRES_USER: ${{ secrets.POSTGRES_USER }}
          POSTGRES_PASSWORD: ${{ secrets.POSTGRES_PASSWORD }}
          MONGO_URI: ${{ secrets.MONGO_URI }}

      - name: Análisis SonarCloud
        run: mvn sonar:sonar -Dsonar.token=${{ secrets.SONAR_TOKEN }}
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}

  deploy:
    name: Deploy a Azure
    runs-on: ubuntu-latest
    needs: build-and-analyze
    if: github.ref == 'refs/heads/main'  # solo desde main

    steps:
      - uses: actions/checkout@v4

      - name: Configurar Java 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven

      - name: Empaquetar JAR
        run: mvn clean package -DskipTests

      - name: Deploy a Azure Web App
        uses: azure/webapps-deploy@v3
        with:
          app-name: 'dosw-parcial-t2-app'
          publish-profile: ${{ secrets.AZURE_WEBAPP_PUBLISH_PROFILE }}
          package: target/*.jar
```

### Secrets requeridos en GitHub

Ve a tu repositorio → **Settings → Secrets and variables → Actions** y agrega:

| Secret | Cómo obtenerlo |
|--------|----------------|
| `SONAR_TOKEN` | SonarCloud → Account → Security → Generate Token |
| `POSTGRES_URL` | Tu URL de conexión PostgreSQL |
| `POSTGRES_USER` | Usuario de PostgreSQL |
| `POSTGRES_PASSWORD` | Contraseña de PostgreSQL |
| `MONGO_URI` | URI de MongoDB Atlas |
| `AZURE_WEBAPP_PUBLISH_PROFILE` | Azure Portal → App Service → "Get publish profile" → pega el XML completo |

---

### Resumen de comandos rápidos

| Acción | Comando |
|--------|---------|
| Solo tests | `mvn test` |
| Tests + JaCoCo | `mvn clean verify` |
| Sonar + JaCoCo | `mvn clean verify sonar:sonar -Dsonar.token=TOKEN` |
| Build JAR | `mvn clean package -DskipTests` |
| Deploy Azure CLI | `az webapp deploy --src-path target/*.jar --type jar ...` |
| Build Docker local | `docker build -t dosw-parcial-t2 .` |
| Run Docker local | `docker run -p 8080:8080 dosw-parcial-t2` |

:D