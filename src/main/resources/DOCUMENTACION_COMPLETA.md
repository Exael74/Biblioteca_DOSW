# ARQUITECTURA HEXAGONAL EN SPRING BOOT: GUÍA DEFINITIVA DE CONSTRUCCIÓN DESDE CERO

Este documento es el **plano arquitectónico definitivo** de `Biblioteca_DOSW`. Está diseñado como un manual exhaustivo y técnico que documenta línea por línea y archivo por archivo cómo inicializar, estructurar y programar este sistema completo (Dominio, Adaptadores, Base de Datos y Seguridad JWT) desde cero.

---

## 🏗️ FASE 1: INICIALIZACIÓN Y CONFIGURACIÓN DEL PROYECTO

### 1.1 Configuración de Spring Initializr
Para arrancar el proyecto, las dependencias fundamentales en tu archivo `pom.xml` (Spring Boot 3.x) son:
- **`spring-boot-starter-web`**: Para los Controladores REST.
- **`spring-boot-starter-data-jpa`**: Para persistencia relacional con Hibernate.
- **`postgresql`**: Driver de la base de datos SQL.
- **`spring-boot-starter-validation`**: Para `@NotNull`, `@Size` en DTOs.
- **`spring-boot-starter-security`**: El motor de acceso y filtros.
- **`lombok`**: Para auto-generar getters, setters y constructores.

A esto, se deben agregar manualmente las librerías modernas de JWT (`jjwt`) y Swagger (OpenAPI):
```xml
<!-- JWT Moderno -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
</dependency>

<!-- Documentación Interactiva -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.2.0</version>
</dependency>
```

### 1.2 Estructura de Directorios (El Hexágono)
Crea y respeta rígidamente esta estructura dentro de `src/main/java/edu/eci/dosw/tdd`:
```text
edu.eci.dosw.tdd
 ├── config/            # Beans globales y Configs
 ├── controller/        # Adaptadores Inbound (APIs)
 │   └── dto/           # Objetos de tráfico de red
 ├── core/              # EL NÚCLEO (Independiente)
 │   ├── model/         # Clases Planas de Java
 │   └── service/       # Casos de Uso
 ├── exception/         # Handlers de Errores Globales
 ├── persistence/       # Adaptadores Outbound (Bases de datos)
 │   ├── relational/    # Entidades y Repos JPA
 │   └── nonrelational/ # Documentos y Repos Mongo
 ├── security/          # Filtros y Providers de JWT
 └── util/              # Utilidades transversales
```

### 1.3 `application.yaml` (Conexión y Entorno)
Ubica esto en `src/main/resources`. Contiene los secretos criptográficos y los conectores.
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/library_db
    username: my_user
    password: my_password
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update # Autocrea/actualiza las tablas a partir del código de Java
    show-sql: true
server:
  port: 8080
security:
  jwt:
    # Secreto firmado (¡Usa claves largas de 256 bits!)
    secret: ZGV2c2VjcmV0LWRldnNlY3JldC1kZXZzZWNyZXQtZGV2c2VjcmV0
    expiration-ms: 3600000 # 1 hora
```

---

## 🧠 FASE 2: EL NÚCLEO (CORE) Y PUERTOS (DE ADENTRO HACIA AFUERA)

El Core es el corazón de la Arquitectura Hexagonal. **Regla de Oro:** Ningún archivo dentro de `core` puede importar clases de Spring Web (`@RestController`), Spring Data (`@Entity`, `JpaRepository`) ni bases de datos. Solamente lógica pura.

### 2.1 El Modelo Puro (`core/model/Book.java`)
```java
package edu.eci.dosw.tdd.core.model;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Book {
    private Long id;
    private String title;
    private String isbn;
    private boolean available;
}
```

### 2.2 El Puerto / Port (`persistence/BookRepository.java`)
Es la interfaz base. El Core dicta *qué operaciones de guardado necesita*, pero le importa un carajo si se guardará en PostgreSQL, MongoDB o en un Excel.
```java
package edu.eci.dosw.tdd.persistence;
import edu.eci.dosw.tdd.core.model.Book;

public interface BookRepository {
    Book save(Book book);
    boolean existsByIsbn(String isbn);
}
```

### 2.3 El Servicio (`core/service/BookService.java`)
Contiene la regla de negocio. Llama al "Puerto" a ciegas.
```java
package edu.eci.dosw.tdd.core.service;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.persistence.BookRepository;
// Imports de Spring Core permitidos
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService {
    
    // Inyectamos la Interfaz, NUNCA un repositorio de Hibernate
    private final BookRepository bookRepository;

    public Book create(Book book) {
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new RuntimeException("ISBN duplicado. No permitido.");
        }
        return bookRepository.save(book);
    }
}
```

---

## 🗄️ FASE 3: EL ADAPTADOR DE PERSISTENCIA (OUTBOUND ADAPTER)

En esta capa, tomamos el bloque físico (Hibernate) y lo conectamos al Puerto del Core.

### 3.1 La Entidad SQL (`persistence/relational/entity/BookEntity.java`)
```java
package edu.eci.dosw.tdd.persistence.relational.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "books")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class BookEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(unique = true, nullable = false)
    private String isbn;
    
    private boolean available;
}
```

### 3.2 Repositorio de Spring Data JPA
Esto generará los Queries SQL tras bambalinas.
```java
package edu.eci.dosw.tdd.persistence.relational;
import edu.eci.dosw.tdd.persistence.relational.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataJpaBookRepository extends JpaRepository<BookEntity, Long> {
    boolean existsByIsbn(String isbn);
}
```

### 3.3 El Adaptador (`persistence/relational/BookAdapter.java`)
**Este es el puente real.** Implementa la interfaz central, captura la variable, la convierte y la envía contra PostgreSQL.
```java
package edu.eci.dosw.tdd.persistence.relational;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.persistence.BookRepository;
import edu.eci.dosw.tdd.persistence.relational.entity.BookEntity;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BookAdapter implements BookRepository {

    private final SpringDataJpaBookRepository jpaRepository;

    @Override
    public Book save(Book book) {
        // Mapeo (Modelo Puro -> Entidad SQL)
        BookEntity entity = new BookEntity(book.getId(), book.getTitle(), book.getIsbn(), book.isAvailable());
        
        // Guardando en Base de datos
        BookEntity savedEntity = jpaRepository.save(entity);
        
        // Mapear de vuelta (Entidad SQL -> Modelo Puro)
        return Book.builder()
                .id(savedEntity.getId())
                .title(savedEntity.getTitle())
                .isbn(savedEntity.getIsbn())
                .available(savedEntity.isAvailable())
                .build();
    }

    @Override
    public boolean existsByIsbn(String isbn) {
        return jpaRepository.existsByIsbn(isbn);
    }
}
```

---

## 🌐 FASE 4: EL CONTROLADOR Y DTO (INBOUND ADAPTER)

Esta es la cara pública del sistema, la cual interactuará vía JSON.

### 4.1 El DTO (`controller/dto/BookDto.java`)
Objeto de transferencia de datos con Validaciones estrictas de red.
```java
package edu.eci.dosw.tdd.controller.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BookDto {
    @NotBlank(message = "El titulo no puede estar vacio")
    private String title;
    
    @NotBlank(message = "El ISBN es obligatorio para registrar")
    private String isbn;
}
```

### 4.2 El Controlador (`controller/BookController.java`)
El controlador intercepta la solicitud, convierte el DTO a Modelo Puro y se lo lanza al `BookService`.
```java
package edu.eci.dosw.tdd.controller;
import edu.eci.dosw.tdd.controller.dto.BookDto;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<BookDto> createBook(@Valid @RequestBody BookDto dto) {
        // Mapear desde la Red al Core
        Book newBookModel = Book.builder()
                            .title(dto.getTitle())
                            .isbn(dto.getIsbn())
                            .available(true)
                            .build();

        // Ejecutar Regla de Negocio
        Book savedBook = bookService.create(newBookModel);

        // Mapear respuesta hacia la Red
        BookDto responseDto = new BookDto();
        responseDto.setTitle(savedBook.getTitle());
        responseDto.setIsbn(savedBook.getIsbn());

        return ResponseEntity.ok(responseDto);
    }
}
```

---

## 🛡️ FASE 5: LA COBERTURA DE SEGURIDAD (SPRING SECURITY + JWT)

Para lograr que el servidor valide quién es el usuario y qué roles tiene, construimos los interceptores `Stateless`.

### 5.1 Generación del Token (`util/SecurityUtils.java` o `JwtProvider`)
Clase encargada de construir matemáticamente el String del token y también validarlo pasadas las horas.
```java
package edu.eci.dosw.tdd.util;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class SecurityUtils {

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String username, String role) {
        return Jwts.builder()
                .subject(username)
                .claim("role", role) // Inyección de Rol (Ej. LIBRARIAN)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public String getUserNameFromToken(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build()
                .parseSignedClaims(token).getPayload().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
```

### 5.2 El Interceptor Principal (`security/JwtAuthenticationFilter.java`)
Es el muro del castillo. Atrapa toda petición HTTP. Si no hay token, bloquea el camino.
```java
package edu.eci.dosw.tdd.security;
import edu.eci.dosw.tdd.util.SecurityUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final SecurityUtils jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Quitar 'Bearer '
            
            if (jwtProvider.validateToken(token)) {
                String username = jwtProvider.getUserNameFromToken(token);
                // Si el token es limpio, configuramos el Contexto de Spring.
                UsernamePasswordAuthenticationToken auth = 
                     new UsernamePasswordAuthenticationToken(username, null, null);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        // Permitimos el paso para que Spring evalue si tenia roles en la Config.
        filterChain.doFilter(request, response);
    }
}
```

### 5.3 El Gestor de Tráfico (`security/SecurityConfig.java`)
Donde defines qué URL está protegida y quién tiene autorización.
```java
package edu.eci.dosw.tdd.config;
import edu.eci.dosw.tdd.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Stateless APIs no necesitan CSRF
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll() // Login Público
                .requestMatchers("/api/books/**").authenticated() // Bloqueado, requiere Token
                .anyRequest().authenticated()
            )
            // Añadir nuestra muralla personalizada antes de la estándar de Spring
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }
}
```

---

## ⛔ FASE 6: EL MANEJADOR GLOBAL DE ERRORES

Finalmente, la cereza del pastel para sistemas empresariales: Interceptar las excepciones para que nunca arrojemos los espantosos Stacktraces de Java en la cara de los consumidores del API HTTP.

`exception/GlobalExceptionHandler.java`:
```java
package edu.eci.dosw.tdd.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Maneja los throw new RuntimeException("...") de nuestro Core
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleBusinessError(RuntimeException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // Maneja las violaciones de los @Valid en los Controladores
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidations(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
```

---

## 📈 FASE 7: ANÁLISIS DE CALIDAD (JACOCO Y SONARQUBE)

Una vez construido el sistema, es indispensable medir la calidad del código. El proyecto está configurado para utilizar **JaCoCo** (Java Code Coverage) para medir el % de código cubierto por pruebas, y **SonarQube** para el análisis estático (bugs, vulnerabilidades, code smells).

### 7.1 Configuración Previa en `pom.xml`
Asegúrate de que en la sección `<properties>` de tu POM tengas la ruta donde JaCoCo soltará los reportes:
```xml
<properties>
  <sonar.java.coveragePlugin>jacoco</sonar.java.coveragePlugin>
  <sonar.dynamicAnalysis>reuseReports</sonar.dynamicAnalysis>
  <sonar.jacoco.reportPath>${project.basedir}/target/jacoco.exec</sonar.jacoco.reportPath>
  <sonar.language>java</sonar.language>
  <sonar.coverage.jacoco.xmlReportPaths>${project.basedir}/target/site/jacoco/jacoco.xml</sonar.coverage.jacoco.xmlReportPaths>
</properties>
```
Y en la sección de `<plugins>`:
```xml
<plugin>
  <groupId>org.jacoco</groupId>
  <artifactId>jacoco-maven-plugin</artifactId>
  <version>0.8.12</version>
  <executions>
    <execution>
      <id>prepare-agent</id>
      <goals><goal>prepare-agent</goal></goals>
    </execution>
    <execution>
      <id>report</id>
      <phase>test</phase>
      <goals><goal>report</goal></goals>
    </execution>
  </executions>
</plugin>
<plugin>
  <groupId>org.sonarsource.scanner.maven</groupId>
  <artifactId>sonar-maven-plugin</artifactId>
  <version>3.11.0.3922</version>
</plugin>
```

### 7.2 Paso a Paso para Generar el Reporte de JaCoCo

1. **Escribir Pruebas:**
   JaCoCo solo mide lo que se testea. Asegúrate de tener tests unitarios (`@Test`) construidos con `JUnit` y `Mockito` en tu carpeta `src/test/java`.
2. **Ejecutar las Pruebas con Maven:**
   Abre una terminal en la raíz del proyecto y corre el siguiente comando:
   ```bash
   mvn clean test
   ```
3. **Verificar la Generación:**
   Al finalizar, dirígete a la carpeta `target/site/jacoco` y abre el archivo **`index.html`** en cualquier navegador web. Allí verás visualmente (en verde y rojo) exactamente qué líneas de tu código pasaron y cuáles faltaron por cubrir.

### 7.3 Paso a Paso para Subir el Análisis a SonarQube

SonarQube requiere tener un un perfil corriendo al cual Maven enviará los reportes de JaCoCo.

1. **Levantar SonarQube (Instancia Local con Docker):**
   Si no tienes servidor, levanta uno rápido corriendo en la terminal:
   ```bash
   docker run -d --name sonarqube -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true -p 9000:9000 sonarqube:latest
   ```
2. **Entrar al Dashboard:**
   Abre tu navegador en `http://localhost:9000` (El usuario y contraseña por defecto es `admin` / `admin`).
   - Crea un proyecto local manualmente (`Biblioteca_DOSW`).
   - Sonar te entregará un **Token de autenticación** (Ej. `sqp_12345abcdef...`).
3. **Ejecutar el Scanner desde el Proyecto:**
   Abre la terminal en la carpeta principal de tu código (donde se ubica el `pom.xml`). Ejecuta los test primero (para generar JaCoCo XML) y luego limpia y despacha el escáner a SonarQube:
   ```bash
   mvn clean test
   
   mvn sonar:sonar \
     -Dsonar.host.url=http://localhost:9000 \
     -Dsonar.login=EL_TOKEN_QUE_TE_DIO_SONAR \
     -Dsonar.projectKey=Biblioteca_DOSW
   ```
4. **Inspeccionar Resultados:**
   Recarga tu dashboard web. Ahí podrás observar tu Nivel de Cobertura de Pruebas, Nivel de Deuda Técnica, Vulnerabilidades detectadas y "Code Smells".
