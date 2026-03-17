# DOSW Company - Sistema de Gestión de Bibliotecas

Este repositorio contiene la base del sistema de gestión de bibliotecas para **DOSW Company**. El sistema está diseñado para gestionar libros, usuarios y préstamos de forma robusta, asegurando la disponibilidad de los ejemplares y registrando la actividad mediante un patrón de arquitectura en capas y siguiendo metodologías de calidad.

## Requerimientos de Funcionalidad

- **Gestión de Libros:** Agregar libros, listar todos los ejemplares, buscar por código de identificación y actualizar su estado de disponibilidad.
- **Gestión de Usuarios:** Registrar nuevos usuarios, listar los existentes y buscar por identificación.
- **Gestión de Préstamos:** Manejo del listado de usuarios, un listado de préstamos y un Mapa de Libros (relacionando el libro con la cantidad de ejemplares disponibles).

## Arquitectura del Sistema

El proyecto sigue un patrón de arquitectura en capas definido dentro del paquete `edu.eci.dosw.tdd`.

### 1. Diagrama de Componentes (General)

Muestra la vista de alto nivel de las capas principales del sistema y cómo interactúan entre sí.

```mermaid
flowchart LR
    %% Interfaz Externa (Front/Cliente)
    Client["Client App / API Client\n(Frontend / REST)"]
    
    %% Componentes del Dominio (Backend / Core)
    subgraph CoreSystem ["LMS Core (edu.eci.dosw.tdd)"]
        direction TB
        Book["Book Component\n(Módulo de Libros)"]
        User["User Component\n(Módulo de Usuarios)"]
        Loan["Loan Component\n(Módulo de Préstamos / Transaction)"]
    end
    
    %% Base de Datos (Almacenamiento en Memoria según requerimientos)
    DB["LMS Database / Storage\n(In-Memory Lists & Maps)"]
    
    %% Relaciones y Dependencias
    Client -->|Requests| Book
    Client -->|Requests| User
    Client -->|Requests| Loan
    
    Loan -.->|Valida y depende de| Book
    Loan -.->|Valida y depende de| User
    
    Book ==>|Lee/Escribe| DB
    User ==>|Lee/Escribe| DB
    Loan ==>|Lee/Escribe| DB
```

### 2. Diagrama de Componentes Específico

Ofrece un nivel de detalle más profundo sobre las clases exactas que conforman cada paquete de la aplicación.

```mermaid
flowchart TB
    %% Nodos principales
    LMS_DB["LMS database\n(Lists & Maps)"]
    Member["Member\n(User Component:\nUserController,\nUserService,\nUser)"]
    Book["Book\n(Book Component:\nBookController,\nBookService,\nBook)"]
    Transaction["Transaction\n(Loan Component:\nLoanController,\nLoanService,\nLoan)"]
    Search["Search\n(Busqueda de Elementos)"]
    
    %% Relaciones
    LMS_DB ---|Acceso a Datos| _Bus
    _Bus(( )) --- Search
    _Bus --- Book
    
    Transaction -.->|Valida / Asigna| Book
    Transaction -.->|Relacionado con| Member
    
    LMS_DB -.- Member
    LMS_DB -.- Transaction
```

## Implementación y Pruebas (Ciclo de Calidad)

Tras la estructuración mostrada arriba y la implementación de un **Manejador de Errores Global (Global Error Handler)**, se debe seguir el ciclo:

1. **Establecer escenarios de prueba:** Redactar casos de éxito y de fallo/error.
2. **Crear clases de prueba con JUnit:** Aplicar TDD en la construcción del core.
3. **Ejecutar Pruebas y Análisis:** Realizar medición de cobertura (coverage) y análisis estático de código.
