# 🗄️ Guía de Configuración de Bases de Datos

Proyecto: **DOSW-ParcialT2**  
Bases de datos: **PostgreSQL** (relacional) y **MongoDB** (no relacional)

---

## 📌 Tabla de Contenidos

1. [PostgreSQL (JPA - Persistencia Relacional)](#1-postgresql-jpa---persistencia-relacional)
   - [Instalación](#instalación-postgresql)
   - [Creación de la base de datos y usuario](#creación-de-la-base-de-datos-y-usuario)
   - [Configuración en application.yaml](#configuración-en-applicationyaml-postgresql)
2. [MongoDB (Persistencia No Relacional)](#2-mongodb-persistencia-no-relacional)
   - [Instalación](#instalación-mongodb)
   - [Creación de la base de datos y usuario](#creación-de-la-base-de-datos-y-usuario-mongo)
   - [Configuración en application.yaml](#configuración-en-applicationyaml-mongodb)
3. [Verificación de conexiones](#3-verificación-de-conexiones)
4. [Variables de entorno (recomendado)](#4-variables-de-entorno-recomendado)

---

## 1. PostgreSQL (JPA - Persistencia Relacional)

### Instalación (PostgreSQL)

**Opción A — Instalación local:**
1. Descarga el instalador desde: https://www.postgresql.org/download/windows/
2. Durante la instalación:
   - Puerto por defecto: `5432`
   - Anota la contraseña del usuario `postgres` (superusuario)
3. Asegúrate de que el servicio esté activo:
   ```
   Servicios de Windows → postgresql-x64-XX → Iniciado
   ```

**Opción B — Docker (recomendado):**
```bash
docker run --name parcial-postgres \
  -e POSTGRES_USER=myuser \
  -e POSTGRES_PASSWORD=mypassword \
  -e POSTGRES_DB=parcial_db \
  -p 5432:5432 \
  -d postgres:16
```
> Con esto ya tienes la DB lista, omite el paso de "Creación de usuario" abajo.

---

### Creación de la base de datos y usuario

Conéctate con el cliente `psql` (o pgAdmin) usando el usuario `postgres`:

```sql
-- 1. Crear el usuario de la aplicación
CREATE USER myuser WITH PASSWORD 'mypassword';

-- 2. Crear la base de datos
CREATE DATABASE parcial_db OWNER myuser;

-- 3. Otorgar privilegios
GRANT ALL PRIVILEGES ON DATABASE parcial_db TO myuser;
```

> Si usas **pgAdmin**: haz clic derecho en "Login/Group Roles" → Create → Login/Group Role, completa los campos y luego crea la DB asociándola al rol creado.

---

### Configuración en application.yaml (PostgreSQL)

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/parcial_db   # nombre de la BD
    username: myuser                                    # usuario creado arriba
    password: mypassword                               # contraseña del usuario
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update   # 'create' la primera vez si quieres que cree las tablas
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

| Propiedad | Descripción |
|-----------|-------------|
| `url` | Dirección de conexión JDBC. Cambia `localhost` si usas un host remoto |
| `username` | Usuario de PostgreSQL con permisos sobre `parcial_db` |
| `password` | Contraseña de ese usuario |
| `ddl-auto: update` | Actualiza el esquema automáticamente con las entidades JPA |
| `ddl-auto: create` | Elimina y recrea las tablas al reiniciar (útil solo en desarrollo) |

---

## 2. MongoDB (Persistencia No Relacional)

### Instalación (MongoDB)

**Opción A — Instalación local:**
1. Descarga la Community Edition desde: https://www.mongodb.com/try/download/community
2. Durante la instalación activa la opción **"Install MongoDB as a Service"**
3. Puerto por defecto: `27017`
4. (Opcional) Instala **MongoDB Compass** para una interfaz gráfica

**Opción B — Docker (recomendado):**
```bash
docker run --name parcial-mongo \
  -e MONGO_INITDB_ROOT_USERNAME=mymongouser \
  -e MONGO_INITDB_ROOT_PASSWORD=mymongopassword \
  -e MONGO_INITDB_DATABASE=parcial_mongo_db \
  -p 27017:27017 \
  -d mongo:7
```

**Opción C — MongoDB Atlas (nube gratuita):**
1. Regístrate en https://www.mongodb.com/cloud/atlas
2. Crea un cluster gratuito (M0)
3. En "Database Access" crea un usuario con contraseña
4. En "Network Access" agrega tu IP (o `0.0.0.0/0` para desarrollo)
5. Obtén la URI de conexión desde "Connect" → "Connect your application"

---

### Creación de la base de datos y usuario (Mongo)

Conéctate con `mongosh` (o MongoDB Compass):

```js
// 1. Cambiar a la base de datos de autenticación
use admin

// 2. Autenticarte como root (si tienes auth habilitado)
db.auth("mymongouser", "mymongopassword")

// 3. Crear el usuario de la aplicación en la BD del proyecto
use parcial_mongo_db

db.createUser({
  user: "mymongouser",
  pwd: "mymongopassword",
  roles: [{ role: "readWrite", db: "parcial_mongo_db" }]
})
```

> MongoDB crea la base de datos automáticamente la primera vez que se inserta un documento, no es necesario crearla manualmente con un comando previo.

---

### Configuración en application.yaml (MongoDB)

**Forma simple (sin autenticación / Docker local):**
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/parcial_mongo_db
```

**Forma con autenticación (usuario/contraseña):**
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/parcial_mongo_db
      username: mymongouser
      password: mymongopassword
      authentication-database: admin
```

**Forma con MongoDB Atlas (nube):**
```yaml
spring:
  data:
    mongodb:
      uri: mongodb+srv://<usuario>:<contraseña>@cluster0.xxxxx.mongodb.net/parcial_mongo_db?retryWrites=true&w=majority
```

> ⚠️ Reemplaza `<usuario>`, `<contraseña>` y el host del cluster con los valores reales de Atlas.

| Propiedad | Descripción |
|-----------|-------------|
| `uri` | Cadena completa de conexión (tiene prioridad sobre los campos individuales) |
| `username` | Usuario de MongoDB |
| `password` | Contraseña del usuario |
| `authentication-database` | BD donde está definido el usuario (normalmente `admin`) |

---

## 3. Verificación de conexiones

Una vez configurado el `application.yaml`, arranca el proyecto con:

```bash
mvn spring-boot:run
```

Busca en los logs estas líneas que confirman las conexiones:

**PostgreSQL ✅**
```
HikariPool-1 - Start completed.
```

**MongoDB ✅**
```
Opened connection [connectionId{...}] to localhost:27017
```

Si hay un error de conexión verás:
- `Connection refused` → el servicio no está corriendo
- `FATAL: password authentication failed` → credenciales incorrectas
- `UnknownHostException` → hostname incorrecto

---

## 4. Variables de entorno (recomendado)

Para no exponer credenciales en el archivo de configuración, usa variables de entorno:

**`application.yaml` con variables de entorno:**
```yaml
spring:
  datasource:
    url: ${POSTGRES_URL:jdbc:postgresql://localhost:5432/parcial_db}
    username: ${POSTGRES_USER:myuser}
    password: ${POSTGRES_PASSWORD:mypassword}
  data:
    mongodb:
      uri: ${MONGO_URI:mongodb://localhost:27017/parcial_mongo_db}
      username: ${MONGO_USER:mymongouser}
      password: ${MONGO_PASSWORD:mymongopassword}
      authentication-database: ${MONGO_AUTH_DB:admin}

jwt:
  secret: ${JWT_SECRET:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}
  expiration: ${JWT_EXPIRATION:86400000}
```

**Definir las variables en Windows (PowerShell):**
```powershell
$env:POSTGRES_URL    = "jdbc:postgresql://localhost:5432/parcial_db"
$env:POSTGRES_USER   = "myuser"
$env:POSTGRES_PASSWORD = "tu_contraseña"
$env:MONGO_URI       = "mongodb://localhost:27017/parcial_mongo_db"
$env:MONGO_USER      = "mymongouser"
$env:MONGO_PASSWORD  = "tu_contraseña_mongo"
```

> **Nota:** La sintaxis `${VAR:default}` significa "usa la variable de entorno `VAR`; si no existe, usa el valor por defecto después de `:`". Esto permite que el proyecto funcione en local sin configurar nada extra, y en producción/CI se usan las variables del servidor.

---

> ⚠️ **Importante:** Nunca subas credenciales reales a GitHub. Agrega `application.yaml` al `.gitignore` si contiene contraseñas en texto plano, o usa siempre el enfoque de variables de entorno descrito en la sección 4.
