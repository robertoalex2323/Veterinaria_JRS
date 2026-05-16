# 📚 GUÍA PASO A PASO - Neon + Veterinaria Pet Clinic

## Paso 1: Acceder a Neon Console

1. Ve a https://console.neon.tech
2. Inicia sesión con tu cuenta Neon
3. Selecciona tu proyecto "petclinic" (o el nombre de tu proyecto)

```
┌─────────────────────────────────────────┐
│         NEON CONSOLE                    │
├─────────────────────────────────────────┤
│                                         │
│  Mi Proyecto                            │
│  ├─ Development (Compute)               │
│  ├─ neondb (Database)                   │
│  └─ SQL Editor                          │
│                                         │
└─────────────────────────────────────────┘
```

---

## Paso 2: Abrir el SQL Editor

En la interfaz de Neon:
1. Haz clic en "SQL Editor" en el panel izquierdo
2. O busca "SQL Editor" en el top bar

```
┌─────────────────────────────────────────┐
│         SQL EDITOR - NEON               │
├─────────────────────────────────────────┤
│                                         │
│  Database: neondb                       │
│  User: neondb_owner                     │
│                                         │
│  ┌─────────────────────────────────┐   │
│  │ -- Pega aquí el script SQL      │   │
│  │ CREATE TABLE usuarios ( ...     │   │
│  │ ...                             │   │
│  └─────────────────────────────────┘   │
│                                         │
│  [ Ejecutar ] (Ctrl + Enter)            │
│                                         │
└─────────────────────────────────────────┘
```

---

## Paso 3: Copiar el Script SQL

Abre el archivo: `NEON_DATABASE_SETUP.sql`

Selecciona TODO el contenido (Ctrl + A) y cópialo (Ctrl + C).

El script incluye:
- ✅ 12 tablas (usuarios, clientes, mascotas, etc.)
- ✅ Índices para optimizar queries
- ✅ Foreign keys con constraints
- ✅ 5 usuarios iniciales
- ✅ 6 vacunas predeterminadas

---

## Paso 4: Pegar en Neon SQL Editor

1. Ve al SQL Editor en Neon
2. Limpia el área de texto (si hay algo)
3. Pega el contenido del script (Ctrl + V)
4. Deberías ver el script completo en el editor

```
-- Script SQL para Neon Database - Veterinaria Pet Clinic
-- ============================================================

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    ...
```

---

## Paso 5: Ejecutar el Script

1. Haz clic en el botón **"Execute"** en la esquina inferior derecha
2. O presiona **Ctrl + Enter**
3. Espera a que se complete (normalmente 5-15 segundos)

Deberías ver un mensaje como:
```
✓ Query executed successfully
Rows affected: 0
Duration: 2.34s
```

---

## Paso 6: Verificar las Tablas Creadas

Después de ejecutar el script:

1. Ve al panel izquierdo de Neon
2. Expande "Tables" o actualiza (F5)
3. Deberías ver las 12 tablas:

```
Tables
├── usuarios ✓
├── clientes ✓
├── mascotas ✓
├── vacunas ✓
├── consultas ✓
├── signos_vitales ✓
├── seguimientos ✓
├── vacunas_aplicadas ✓
├── alertas_criticas ✓
├── citas ✓
├── pagos ✓
└── agenda ✓
```

---

## Paso 7: Verificar Datos Iniciales

Ejecuta esta query en el SQL Editor de Neon:

```sql
SELECT * FROM usuarios;
```

Deberías ver 5 usuarios:

| username | nombre | rol |
|----------|--------|-----|
| admin | Administrador | ADMIN |
| vet_juan | Juan García | VETERINARIO |
| vet_maria | María López | VETERINARIO |
| recv_carlos | Carlos Rodríguez | RECEPCIONISTA |
| recv_ana | Ana Martínez | RECEPCIONISTA |

También verifica vacunas:

```sql
SELECT * FROM vacunas;
```

Deberías ver 6 vacunas diferentes.

---

## Paso 8: Verificar application.properties

Abre el archivo:
```
src/main/resources/application.properties
```

Verifica que contenga:

```properties
# PostgreSQL - NEON Configuration
spring.datasource.url=jdbc:postgresql://ep-cool-shape-aqja9v6c-pooler.c-8.us-east-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require
spring.datasource.username=neondb_owner
spring.datasource.password=npg_niRekK7UZ9mD
spring.datasource.driver-class-name=org.postgresql.Driver
```

✅ **Ya está configurado** - No necesitas cambios.

---

## Paso 9: Compilar la Aplicación

Abre PowerShell en la carpeta del proyecto y ejecuta:

```powershell
.\mvnw.cmd clean install
```

Esto:
- Limpia compilaciones anteriores
- Descarga dependencias
- Compila todo el proyecto

Deberías ver al final:
```
[INFO] BUILD SUCCESS
[INFO] Total time: XX.XXs
```

---

## Paso 10: Ejecutar la Aplicación

En la misma PowerShell, ejecuta:

```powershell
.\mvnw.cmd spring-boot:run
```

Espera a ver este mensaje:

```
Started VeterinariaPetClinicApplication in X.XXX seconds
Tomcat started on port 8080 (http) with context path '/'
```

---

## Paso 11: Acceder a la Aplicación

Abre tu navegador y ve a:

```
http://localhost:8080
```

Si todo está bien, verás la **página de login**.

---

## Paso 12: Iniciar Sesión

Usa cualquiera de estos usuarios:

**Admin:**
- Usuario: `admin`
- Contraseña: `password123`

**Recepcionista:**
- Usuario: `recv_carlos`
- Contraseña: `password123`

**Veterinario:**
- Usuario: `vet_juan`
- Contraseña: `password123`

---

## ✅ Checklist Final

- [ ] Abrí Neon Console
- [ ] Ejecuté el script SQL en Neon
- [ ] Verifiqué que las 12 tablas existan
- [ ] Verifiqué que los 5 usuarios estén creados
- [ ] Actualicé `application.properties` (ya está hecho)
- [ ] Ejecuté `mvn clean install`
- [ ] Ejecuté `mvn spring-boot:run`
- [ ] Accedí a http://localhost:8080
- [ ] Inicie sesión correctamente
- [ ] Veo el dashboard

---

## 🔍 Diagnosticar Problemas

### Si no conecta a Neon:

**Error**: `Connection refused` o `timeout`

**Checklist**:
```powershell
# 1. Verifica que Neon está activo en la web
# 2. Verifica la contraseña es correcta
# 3. Verifica la URL contiene ?sslmode=require

# 3. Para ver logs detallados:
.\mvnw.cmd spring-boot:run -Dspring.jpa.show-sql=true
```

### Si las tablas no existen:

**Error**: `Relation "usuarios" does not exist`

**Solución**:
1. Abre Neon SQL Editor
2. Ejecuta el script nuevamente
3. Verifica en el panel de "Tables"

### Si el login no funciona:

**Error**: `Invalid username or password`

**Checklist**:
- Usuarios correctos: `admin`, `vet_juan`, `recv_carlos`, etc.
- Contraseña: `password123`
- Verifica mayúsculas/minúsculas (usuarios son en minúsculas)

---

## 📊 Comandos SQL Útiles

```sql
-- Ver todas las tablas
SELECT table_name FROM information_schema.tables WHERE table_schema='public';

-- Contar usuarios
SELECT COUNT(*) FROM usuarios;

-- Ver estructura de una tabla
\d usuarios

-- Ver todos los usuarios
SELECT id, username, nombre, rol FROM usuarios;

-- Ver clientes registrados
SELECT * FROM clientes;

-- Ver mascotas
SELECT * FROM mascotas;
```

---

## 🚀 Próximos Pasos

1. Crea un cliente (Client)
2. Crea una mascota para ese cliente
3. Agrega una cita
4. Registra una consulta
5. Aplica una vacuna
6. Genera un pago

---

## 📞 Soporte

Si algo falla:

1. **Revisa los logs** de la aplicación
2. **Verifica las tablas** existen en Neon
3. **Verifica credenciales** en `application.properties`
4. **Neon Support**: https://neon.tech/support
5. **Stack Overflow**: Busca "Spring Boot + Neon PostgreSQL"

---

**¡Listo! Veterinaria Pet Clinic está corriendo con Neon Database** 🎉
