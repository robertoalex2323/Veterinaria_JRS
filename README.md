Sistema Inteligente de Gestión y Monitoreo Veterinario
 
**Spring Boot · Java · Thymeleaf · PostgreSQL**

 Descripción del Proyecto

Este sistema está diseñado para modernizar la gestión de una clínica veterinaria mediante una plataforma web inteligente.  
El **Módulo Administrador** funciona como el eje principal del sistema, ya que permite supervisar, controlar y administrar las funcionalidades generales del proyecto.

Esta rama está pensada para actuar como una rama integradora, vinculándose progresivamente con los demás módulos del sistema, como recepcionista, veterinario, clientes, pagos, citas y reportes.

> ⚠️ Proyecto en desarrollo: actualmente el sistema se encuentra incompleto y será integrado con las demás ramas conforme avance la implementación.

---

Funcionalidades Clave - Administrador

| Proceso | Descripción |
|  Seguridad y Acceso | Control de autenticación mediante roles de usuario con Spring Security. |
|  Gestión de Usuarios | Administración de cuentas del sistema según su rol: administrador, recepcionista, veterinario y cliente. |
|  Control General de la Clínica | Supervisión de clientes, mascotas, citas, pagos e historiales clínicos. |
|  Gestión de Citas | Visualización y control general de la agenda veterinaria. |
|  Supervisión de Pagos | Revisión de pagos registrados y estados de facturación. |
|  Dashboard Administrativo | Visualización de métricas generales del sistema y actividad de la clínica. |
|  Integración de Módulos | Rama preparada para vincularse con las demás ramas del proyecto. |



 Arquitectura del Sistema

El proyecto sigue el patrón **MVC**:

 Backend - Java y Spring Boot

- `config/`: configuración de seguridad, roles e inicialización de datos.
- `controller/`: gestión de rutas principales del sistema.
- `model/`: entidades como Usuario, Cliente, Mascota, Cita, Pago, Rol, etc.
- `repository/`: interfaces JPA para conexión con PostgreSQL.
- `service/`: lógica de negocio para usuarios, citas, pagos y reportes.

Frontend - Thymeleaf y Recursos Estáticos

- `templates/`: vistas del administrador, dashboard, formularios y gestión de módulos.
- `static/css/`: estilos personalizados para panel administrativo, sidebar y tablas.
- `static/js/`: scripts para interactividad, validaciones y gráficos.

---

 Hoja de Ruta del Proyecto

Unidad 1: Base del Sistema

- Configuración del repositorio GitHub.
- Creación de la estructura MVC.
- Implementación del login y roles.
- Desarrollo inicial del módulo administrador.
- Gestión básica de usuarios.

Unidad 2: Integración de Módulos

- Vinculación con módulo recepcionista.
- Control de clientes y mascotas.
- Integración con agenda de citas.
- Supervisión de pagos.
- Validación de permisos según roles.

Unidad 3: Dashboard y Proyecto Final

- Panel administrativo con métricas generales.
- Reportes de atención, pagos y usuarios.
- Integración completa con las demás ramas.
- Pruebas finales del sistema.
- Presentación del proyecto completo.

---

Stack Tecnológico

- **Backend:** Java 21, Spring Boot, Spring Security
- **Base de Datos:** PostgreSQL
- **Frontend:** HTML5, CSS3, JavaScript, Thymeleaf
- **ORM:** Spring Data JPA
- **Gestión del Proyecto:** Git y GitHub
- **Construcción:** Maven
- **Opcional:** Docker
