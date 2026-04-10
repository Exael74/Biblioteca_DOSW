# Guía de Resolución: Parcial Práctico y Teórico (Caso SportLife)

Este documento detalla paso a paso cómo abordar y resolver el parcial para el sistema **SportLife**, cubriendo exhaustivamente los requerimientos teóricos, de arquitectura, de patrones de diseño y la implementación técnica práctica.

---

## PARTE 1: TEÓRICA

### 1. Matriz de Trazabilidad de Funcionalidades

| ID | Funcionalidad | Prioridad | Bloquea a | Deriva de |
| :--- | :--- | :--- | :--- | :--- |
| **F01** | Registro y Autenticación de Usuario | Alta | F03, F04, F05 | Ninguna |
| **F02** | Gestión de Catálogo de Productos | Alta | F03 | Ninguna |
| **F03** | Añadir Productos al Carrito | Media | F04 | F02 |
| **F04** | Checkout de Carrito (Compra) | Alta | F05 | F01, F03 |
| **F05** | Procesamiento de Pago | Alta | Resta de inventario | F04 |
| **F06** | Historial de Compras | Baja | Ninguna | F05 |

### 2. Definición de Funcionalidades (Ejemplo Estándar para API REST)

**Ejemplo para: Flujo de Compra de un Producto (Checkout)**

*   **Verbo HTTP:** `POST /api/v1/orders`
*   **¿Es idempotente?:** No. Realizar la misma petición dos veces crearía dos órdenes de compra y debitaría dos veces (a menos que se maneje un `Idempotency-Key` en headers, lo cual se recomienda justificar).
*   **Justificación técnica:** Se utiliza POST porque estamos creando un nuevo recurso en el servidor (una Orden) que altera el estado mutando tablas y registros.
*   **Datos de entrada (Request):**
    *   `userId` (UUID, Obligatorio)
    *   `items`: Lista de productos [`productId` (UUID), `quantity` (Integer > 0)] (Obligatorio)
    *   `paymentMethodId` (String, Obligatorio)
*   **Datos de salida (Response):**
    *   `orderId` (UUID)
    *   `status` (String: PENDING/COMPLETED)
    *   `totalAmount` (BigDecimal)
*   **Validaciones:**
    *   **Input:** Que la lista de items no esté vacía, cantidades > 0.
    *   **Negocio:** Verificar stock disponible de los productos, que la tarjeta de crédito sea válida o tenga fondos, que el usuario esté activo.
*   **Códigos HTTP:**
    *   `201 Created`: Orden creada exitosamente (Happy path).
    *   `400 Bad Request`: JSON inválido o cantidades negativas.
    *   `404 Not Found`: El usuario o el producto no existen.
    *   `409 Conflict`: No hay stock suficiente en el inventario.
    *   `500 Internal Server Error`: Caída de base de datos.
    *   `503 Service Unavailable`: Pasarela de pagos externa fuera de servicio.

### 3. Diagramas de Componentes

*   **Diagrama General:** Representa la interacción "Caja Negra" desde el Frontend (Web/Mobile App) a través del API Gateway / Load Balancer, hacia el *Servidor Backend MVC Spring Boot*, y de allí hacia las Bases de Datos (PostgreSQL/MongoDB) y servicios de terceros (Pasarela de pagos).
*   **Diagrama Específico:** Divide el Backend en capas (Controller Layer, Service Component, Auth Filter JWT Component, Data Access layer, Data Mappers, DTOs). 

### 4. Diagrama de Clases y Patrones de Software

*(Regla de Arquitectura: Orientado a Dominios/Capas)*

A continuación se detalla **cuándo aplicar un patrón, por qué elegirlo y cómo construirlo** (generalizado y aplicable a SportLife):

#### A. Patrón DTO (Data Transfer Object)
*   **Cuándo elegirlo:** Para enviar y recibir datos en controladores (`/api/...`) sin exponer las entidades de la Base de Datos.
*   **Por qué elegirlo:** Previene el *Over-fetching* y el ciclo infinito en la serialización JSON. Aumenta la seguridad.
*   **Cómo construirlo:** Crea clases de solo lectura (usando `@Data` o `@Value` / `Records` en Java 17+). Usa un "*Mapper*" (MapStruct o métodos estáticos) para transformar de Objeto de Dominio/Entidad a DTO.

#### B. Patrón Strategy (Comportamiento)
*   **Cuándo elegirlo:** Cuando una funcionalidad se puede ejecutar de múltiples maneras. En SportLife: Los métodos de pago (Tarjeta Crédito, PSE, PayPal, Puntos).
*   **Por qué elegirlo:** Evita los `if-else` o `switch` gigantes. Permite agregar métodos de pago en el futuro sin modificar el código base (Cumple *Open/Closed Principle*).
*   **Cómo construirlo:** 
    1. Crear una Interfaz `PaymentStrategy` con el método `processPayment(amount)`.
    2. Crear clases concretas: `CreditCardStrategy`, `PaypalStrategy` que implementan la interfaz.
    3. Usar un *Contexto*, como `PaymentProcessorService`, que reciba en su constructor/método el `PaymentStrategy` específico a usar.

#### C. Patrón Builder (Creacional)
*   **Cuándo elegirlo:** Al crear objetos muy complejos con múltiples atributos, muchos de ellos opcionales. En SportLife: La entidad `Order` (Orden de Compra) o `Product` (si tiene docenas de variantes, pesos, dimensiones, tallas).
*   **Por qué elegirlo:** Evita los constructores con muchos parámetros (anti-patrón *Telescoping Constructor*). Hace que la instanciación sea más legible.
*   **Cómo construirlo:** Añadiendo la anotación `@Builder` de Lombok a la clase, o mediante una clase interna estática `Builder` que retorne `this` en cada método de seteo, y un método final `.build()` que devuelva el objeto.

#### D. Patrón Repository (Estructural/Arquitectónico)
*   **Cuándo elegirlo:** Siempre que se necesite interactuar con la Base de datos en Spring Boot.
*   **Por qué elegirlo:** Abstrae todo el código SQL o Mongo Query. Hace posible hacer Mocks para Unit Testing sin levantar la BD.
*   **Cómo construirlo:** Extendiendo `JpaRepository<T, ID>` o `MongoRepository<T, ID>`.

#### E. Patrón Singleton
*   **Cuándo elegirlo:** Cuando solo necesitamos una única instancia compartida a nivel global de un componente.
*   **Cómo construirlo:** En Spring Boot, todos los `@Service`, `@Controller` y `@Repository` son Singleton por defecto en el contenedor de IoC. Para componentes manuales: usar un constructor privado y un campo estático mediante inicialización temprana o "Double-checked locking".

### 5. Bases de Datos: Relacional vs No Relacional

En SportLife usaremos el patrón de *Polyglot Persistence*:
*   **Modelo Relacional (PostgreSQL):** *Por qué:* Requiere propiedades ACID estancas. Se usa para lo transaccional y rígido.
    *   **Tablas:** `Users` (id, email, password, role), `Orders` (id, user_id, date, total_price, status), `Order_Items` (order_id, product_id, price_frozen).
*   **Modelo No Relacional (MongoDB):** *Por qué:* Esquemas dinámicos, búsquedas rápidas para catálogo.
    *   **Colecciones:** `CatalogProducts` (datos variados, categorías, características flexibles que cambian dependiendo del tipo de deporte, reviews embebidos de los usuarios).

### 6. Seguridad, Roles y Permisos

*   **Identificación de Roles:**
    *   `ROLE_ADMIN`: Acceso a creación/edición de Catálogo, visualización de métricas y todas las órdenes del sistema.
    *   `ROLE_CUSTOMER`: Acceso a visualizar catálogo, comprar (Checkout), ver historial *propio*.
    *   `GUEST` (Sin autenticar): Solo puede listar productos.
*   **Tipo de seguridad:** `JWT (JSON Web Tokens)` sin estado (Stateless).
*   **Ventajas:** Es escalable (no requiere guardar sesión en la DB del servidor). Cada petición lleva el token en la cabecera, protegiendo las rutas fácilmente con Anotaciones en Spring (`@PreAuthorize`).

### 7. Tecnologías Web: TLS/SSL y CORS

*   **CORS (Cross-Origin Resource Sharing):** Es CRÍTICO en las REST API para que navegadores impidan que sitios web maliciosos de terceros consuman la API en nombre del usuario (Prevención de CSRF). Se configura a nivel global en Spring con un `CorsConfigurationSource` habilitando el origin del frontend (ej: `http://localhost:3000`).
*   **TLS/SSL:** Protocolo de cifrado usando certificados digitales para habilitar HTTPS.
    *   *Ventaja:* Evita ataques de escalamiento de privilegios o robos y *Man-In-The-Middle* cifrando los datos viajando entre el cliente y el servidor (contraseñas, datos bancarios).

### 8. Anotaciones Extra (Figma y Tiempos)
*   **Figma:** Deberás bosquejar visualmente pantallas para: 1. Catálogo / 2. Detalle del Producto / 3. Tu Carrito y Checkout.
*   **Tiempos:** Llevar una bitácora en la primera página de cuánto tiempo tomó escribir la justificación de patrones vs armar el Figma vs la Matriz.

---

## PARTE 2: PRÁCTICA

### 1 y 2. Configuración del Proyecto (Maven + Pom.xml)

Crea un proyecto por *Spring Initializr* (`Spring Web`, `Spring Data JPA`, `Spring Data MongoDB`, `Lombok`, `Validation`).

Añade **SonarQube, Jacoco y Swagger** a tu `pom.xml`:

```xml
<!-- Swagger Documentación -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.2.0</version>
</dependency>
<!-- En <build><plugins> -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.12</version>
    <!-- Configurado para phase TEST para generar jacoco.xml/exec -->
</plugin>
<plugin>
    <groupId>org.sonarsource.scanner.maven</groupId>
    <artifactId>sonar-maven-plugin</artifactId>
    <version>3.11.0.3922</version>
</plugin>
```

### 3. Persistencia Relacional y No Relacional

**`application.yml` o `.properties`**:
```yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/sportlife_db
    username: admin
    password: password
  jpa:
    hibernate:
      ddl-auto: update
  data:
    mongodb:
      uri: mongodb://localhost:27017/sportlife_catalog
```
Para MongoDB deberás usar la anotación `@Document(collection="products")` y para JPA `@Entity` y `@Table(name="orders")`.

### 4. Implementación del Código (MVC)
Mantén estrictamente el modelo MVC hexagonal o de capas:
1.  **Diferenciar BD:** Configura un `ProductMongoRepository` y un `OrderJpaRepository`.
2.  **Validaciones:** Emplea `@Valid` en los controllers y `@NotNull`, `@Min(1)` en los DTOs.
3.  **Manejo Global de Errores:** Crea un `@ControllerAdvice` o `@RestControllerAdvice` llamado `GlobalExceptionHandler` para interceptar validaciones de `@Valid` (retornar 400 Bad Request) y de reglas de negocio (`EntityNotFoundException` para lanzar 404).

### 5. Git - Trabajo de Ramas
La metodología será la siguiente para desarrollar por ejemplo la pasarela de pagos:
```bash
# 1. Crear rama desde develop
git checkout develop
git checkout -b feature/pagos-checkout

# 2. Hacer código, tests y commit
git add .
git commit -m "feat: Integración de checkout y patrón Strategy para pagos"

# 3. Merge y eliminación
git checkout develop
git merge feature/pagos-checkout
git branch -d feature/pagos-checkout
git push origin develop
```

### 6. Pipeline: GitHub Actions
Crea el archivo `.github/workflows/ci-cd.yml`:
```yaml
name: SportLife CI Pipeline

on:
  push:
    branches: [ "master", "develop" ]
  pull_request:
    branches: [ "master", "develop" ]

jobs:
  build_test_analyze:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    - name: Build and Test con Maven (Jacoco report)
      run: mvn clean verify -DskipTests=false
    - name: SonarQube Analysis
      run: mvn sonar:sonar -Dsonar.projectKey=sportlife -Dsonar.host.url=TU_SONAR_URL -Dsonar.login=${{ secrets.SONAR_TOKEN }}
    # Si es 'master', se agrega el paso de Despliegue aquí.
```

### 7. Despliegue en Azure DevOps
Hay dos formas recomendadas para desplegar:
*   **Azure App Services (Linux/Java):** Compilas un `.jar` y realizas un pipeline de Azure que arrastra el artifact y lo lanza en el servicio de aplicaciones de Azure.
*   **Docker Container:** Creas un `Dockerfile`, empaquetas el empaque `.jar` desde Github actions o Azure Pipelines, lo subes a un *Azure Container Registry (ACR)* y despliegas en *Azure Container Instances (ACI)* o *Web App for Containers*. Esta vía justifica mejor el uso de Base de Datos externa (Aiven de Postgres, o Azure SQL Server, junto a MongoDB Atlas).

### 8. Comprobación Final (Video)
1. Levantar el app usando la URL pública dispuesta por Azure.
2. Ingresar a `/swagger-ui/index.html` u open-api base.
3. Grabar la creación de un payload. Testear con los Headers la Autenticación (incluir JWT copiado después de generar login).
4. Probar flujos erróneos (Stock = 0, para mostrar los validadores y códigos HTTP correctos en video).
