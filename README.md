# DOSW Company - Sistema de Gestión de Bibliotecas

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


