# 📊 Configuración de Neon Database para Veterinaria Pet Clinic

## 📋 Contenido

Este documento proporciona instrucciones completas para configurar tu aplicación Veterinaria Pet Clinic con Neon Database.

---

## 🔧 Paso 1: Ejecutar el Script SQL en Neon

### Opción A: Usando el Editor SQL de Neon (Recomendado)

1. **Abre Neon Console**
   - Ve a [https://console.neon.tech](https://console.neon.tech)
   - Inicia sesión con tu cuenta

2. **Accede a tu Proyecto y Base de Datos**
   - Selecciona tu proyecto
   - Ve a la sección "SQL Editor"

3. **Copia el Script SQL**
   - Abre el archivo `NEON_DATABASE_SETUP.sql` de este repositorio
   - Copia **todo el contenido**

4. **Ejecuta el Script**
   - Pega el contenido en el editor SQL de Neon
   - Haz clic en "Execute" o presiona `Ctrl + Enter`
   - Espera a que se complete la ejecución

5. **Verifica la Creación**
   - Las tablas deberían aparecer en la sección "Tables" de Neon
   - Deberías ver 12 tablas creadas:
     - usuarios
     - clientes
     - vacunas
     - mascotas
     - consultas
     - signos_vitales
     - seguimientos
     - vacunas_aplicadas
     - alertas_criticas
     - citas
     - pagos
     - agenda

### Opción B: Usando pgAdmin o DBeaver (Alternativa)

1. Conecta con tu herramienta favorita usando:
   - **Host**: ep-cool-shape-aqja9v6c-pooler.c-8.us-east-1.aws.neon.tech
   - **Usuario**: neondb_owner
   - **Contraseña**: npg_niRekK7UZ9mD
   - **Base de datos**: neondb
   - **SSL Mode**: Require

2. Crea una nueva consulta (Query)
3. Copia el contenido de `NEON_DATABASE_SETUP.sql`
4. Ejecuta la consulta

---

## ✅ Paso 2: Verificar la Configuración de la Aplicación

El archivo `application.properties` ha sido actualizado automáticamente con:

```properties
spring.datasource.url=jdbc:postgresql://ep-cool-shape-aqja9v6c-pooler.c-8.us-east-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require
spring.datasource.username=neondb_owner
spring.datasource.password=npg_niRekK7UZ9mD
```

✓ **Ya está configurado** - No necesitas hacer nada más en el código

---

## 🚀 Paso 3: Ejecutar la Aplicación

### En Visual Studio Code

```powershell
.\mvnw.cmd clean install
.\mvnw.cmd spring-boot:run
```

### Con Maven (línea de comandos)

```bash
mvn clean install
mvn spring-boot:run
```

### Acceso a la Aplicación

- **URL**: http://localhost:8080
- **Acceso**: http://localhost:8080/recepcionista/dashboard

---

## 👤 Usuarios Iniciales

El script SQL crea los siguientes usuarios automáticamente:

| Usuario | Contraseña | Rol | Correo |
|---------|-----------|-----|--------|
| admin | password123 | ADMIN | admin@vetclinic.com |
| vet_juan | password123 | VETERINARIO | juan@vetclinic.com |
| vet_maria | password123 | VETERINARIO | maria@vetclinic.com |
| recv_carlos | password123 | RECEPCIONISTA | carlos@vetclinic.com |
| recv_ana | password123 | RECEPCIONISTA | ana@vetclinic.com |

---

## 📊 Estructura de las Tablas

### 1. **usuarios**
- Almacena todos los usuarios del sistema (Admin, Veterinarios, Recepcionistas)
- Campos: id, username, password (encriptado BCrypt), nombre, email, rol, activo, fecha_creacion

### 2. **clientes**
- Dueños de las mascotas
- Campos: id, nombre, telefono (único), email, direccion, fecha_registro

### 3. **mascotas**
- Información de las mascotas
- Campos: id, cliente_id, nombre, especie, raza, sexo, fecha_nacimiento, peso, color, alergias, estado, observaciones, microchip, esterilizado, foto_url

### 4. **vacunas**
- Catálogo de vacunas disponibles
- Campos: id, nombre

### 5. **consultas**
- Consultas veterinarias de las mascotas
- Campos: id, mascota_id, veterinario_id, fecha_consulta, motivo_consulta, observaciones, estado, peso, temperatura, frecuencia_cardiaca, frecuencia_respiratoria

### 6. **signos_vitales**
- Registro de signos vitales de las mascotas
- Campos: id, mascota_id, consulta_id, peso, temperatura, frecuencia_cardiaca, frecuencia_respiratoria, observaciones, fecha_registro

### 7. **seguimientos**
- Seguimientos de consultas/tratamientos
- Campos: id, mascota_id, consulta_id, descripcion, estado, fecha_programada, fecha_realizada, observaciones

### 8. **vacunas_aplicadas**
- Registro de vacunas aplicadas a mascotas
- Campos: id, mascota_id, vacuna_id, veterinario_id, fecha_aplicacion, proxima_dosis, observaciones

### 9. **alertas_criticas**
- Alertas criticas por mascotas
- Campos: id, mascota_id, consulta_id, tipo_alerta, descripcion, prioridad, estado, fecha_creacion, resuelta_por

### 10. **citas**
- Citas agendadas con veterinarios
- Campos: id, mascota_id, veterinario_id, fecha_hora, motivo, estado, observaciones, fecha_creacion

### 11. **pagos**
- Registro de pagos de los clientes
- Campos: id, cita_id, cliente_id, monto, metodo_pago, estado, fecha_pago, comprobante, observaciones

### 12. **agenda**
- Agenda/Disponibilidad de veterinarios
- Campos: id, veterinario_id, fecha, hora_inicio, hora_fin, duracion_turno, disponible

---

## 🔗 Relaciones de Foráneas (Foreign Keys)

```
clientes (1) ──── (M) mascotas
clientes (1) ──── (M) pagos
usuarios (1) ──── (M) consultas (como veterinario)
usuarios (1) ──── (M) citas (como veterinario)
usuarios (1) ──── (M) vacunas_aplicadas (como veterinario)
usuarios (1) ──── (M) agenda (como veterinario)
usuarios (1) ──── (M) alertas_criticas (como resuelta_por)
mascotas (1) ──── (M) citas
mascotas (1) ──── (M) consultas
mascotas (1) ──── (M) signos_vitales
mascotas (1) ──── (M) seguimientos
mascotas (1) ──── (M) vacunas_aplicadas
vacunas (1) ──── (M) vacunas_aplicadas
consultas (1) ──── (M) signos_vitales
consultas (1) ──── (M) seguimientos
consultas (1) ──── (M) alertas_criticas
citas (1) ──── (1) pagos
```

---

## 🧪 Verificar Conexión

### Opción 1: Logs de la Aplicación

Cuando la aplicación se inicia, verás en los logs:

```
HikariPool-1 - Starting...
HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@...
HikariPool-1 - Start completed
Initialized JPA EntityManagerFactory for persistence unit 'default'
```

### Opción 2: SQL Query en Neon

Ejecuta en el SQL Editor de Neon:

```sql
SELECT COUNT(*) as total_usuarios FROM usuarios;
SELECT COUNT(*) as total_clientes FROM clientes;
SELECT COUNT(*) as total_mascotas FROM mascotas;
```

---

## 🛠️ Troubleshooting

### Error: "Connection refused" o "Connection timeout"

**Causa**: Neon requiere conexión con SSL/TLS

**Solución**: Verifica que la URL incluya `?sslmode=require&channel_binding=require`

```properties
# ✓ CORRECTO
spring.datasource.url=jdbc:postgresql://ep-cool-shape-aqja9v6c-pooler.c-8.us-east-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require

# ✗ INCORRECTO
spring.datasource.url=jdbc:postgresql://ep-cool-shape-aqja9v6c-pooler.c-8.us-east-1.aws.neon.tech/neondb
```

### Error: "Tabla no encontrada" después de ejecutar la app

**Causa**: El script SQL no se ejecutó correctamente

**Solución**:
1. Verifica en Neon SQL Editor que las tablas existan
2. Si no existen, copia nuevamente el script `NEON_DATABASE_SETUP.sql` y ejecútalo
3. Verifica que no haya errores de sintaxis en los logs

### Error: "Usuario/Contraseña incorrecta"

**Causa**: Las credenciales en `application.properties` no coinciden con Neon

**Solución**:
1. Abre tu proyecto en Neon Console
2. Ve a "Connection string" en la sección de "Compute" o "Endpoints"
3. Copia la contraseña correcta y actualiza `application.properties`

### La aplicación es lenta

**Causa**: Pool de conexiones muy pequeño o conexión desde lejano

**Solución**: Neon está optimizado pero puedes ajustar en `application.properties`:

```properties
spring.datasource.hikari.maximum-pool-size=10  # Aumenta si necesitas más conexiones concurrentes
spring.datasource.hikari.minimum-idle=2
```

---

## 📝 Notas Importantes

✓ **SSL Requerido**: Neon requiere conexión SSL. La URL ya lo incluye.

✓ **Automatic DDL Update**: La propiedad `spring.jpa.hibernate.ddl-auto=update` permite que Hibernate añada tablas nuevas automáticamente si las modificas en las entidades Java.

✓ **Datos Iniciales**: El script incluye 5 usuarios iniciales. Puedes añadir más datos después.

✓ **Respaldos**: Neon ofrece respaldos automáticos. Verifica tu plan de Neon para más detalles.

✓ **Free Tier**: Si usas el plan gratuito de Neon, ten en cuenta que:
   - Pool size limitado (máximo 5-10 conexiones)
   - La base de datos puede "dormir" después de inactividad
   - Almacenamiento limitado

---

## 🎯 Próximos Pasos

1. ✅ Ejecuta el script SQL en Neon
2. ✅ Verifica la conexión en `application.properties` (ya está hecha)
3. ✅ Ejecuta `mvn clean install && mvn spring-boot:run`
4. ✅ Accede a http://localhost:8080
5. ✅ Inicia sesión con un usuario del script
6. ✅ Prueba funcionalidades (crear clientes, mascotas, etc.)

---

## 📞 Soporte

Si tienes problemas:

1. Verifica los logs de la aplicación (`target/` o consola)
2. Verifica que las tablas existan en Neon SQL Editor
3. Verifica la contraseña en `application.properties`
4. Contacta a soporte de Neon: https://neon.tech/support

---

**¡Listo! Tu aplicación Veterinaria Pet Clinic está configurada para usar Neon Database.** 🚀
