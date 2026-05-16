# 📊 Diagrama de la Base de Datos - Veterinaria Pet Clinic

## Visualización de Entidades y Relaciones

```
┌─────────────────┐
│   USUARIOS      │
├─────────────────┤
│ id (PK)         │
│ username (U)    │
│ password        │
│ nombre          │
│ email           │
│ rol             │
│ activo          │
│ fecha_creacion  │
└────────┬────────┘
         │
    ┌────┴─────────┬──────────────┬──────────────┬─────────────────┐
    │              │              │              │                 │
    │ (1)          │ (1)          │ (1)          │ (1)             │ (1)
    ▼              ▼              ▼              ▼                 ▼
┌─────────┐  ┌──────────┐  ┌────────────┐  ┌────────────┐  ┌──────────────┐
│CONSULTA │  │   CITA   │  │ VACUNAS_AP │  │  AGENDA    │  │ ALERTAS_CRIT │
│ (M)     │  │   (M)    │  │  LICADAS   │  │   (M)      │  │   ICAS(M)    │
├─────────┤  ├──────────┤  │  (M)       │  ├────────────┤  ├──────────────┤
│id       │  │id        │  ├────────────┤  │id          │  │id            │
│mascota_id◄─┤mascota_id│  │id          │  │veterinario │  │mascota_id    │
│(FK)     │  │(FK)      │  │mascota_id  │  │fecha       │  │(FK)          │
│veterinario◄─┤veterinario│  │(FK)        │  │hora_inicio │  │veterinario_id│
│(FK)     │  │(FK)      │  │vacuna_id   │  │hora_fin    │  │(FK)          │
│fecha    │  │fecha_hora│  │(FK)        │  │duracion    │  │tipo_alerta   │
│motivo   │  │motivo    │  │veterinario │  │disponible  │  │descripcion   │
│estatus  │  │estado    │  │(FK)        │  └────────────┘  │prioridad     │
│observ   │  │observ    │  │fecha_aplic │                  │estado        │
└────┬────┘  └────┬─────┘  │proxima_dos │                  │fecha_creacion│
     │            │         │observ      │                  └──────────────┘
     │            └─────────┴────┬───────┘
     │                           │
     │     ┌─────────────────────┴───────────────────────┐
     │     │                                             │
     │     │ (1)                                    (1)  │
     │     ▼                                        ▼    │
     │  ┌──────────────┐                    ┌─────────┐  │
     │  │  MASCOTAS    │◄──────┬────────────│ VACUNAS │  │
     │  │              │   (M) │            │         │  │
     │  ├──────────────┤       │            ├─────────┤  │
     │  │id (PK)       │       │            │id (PK)  │  │
     │  │cliente_id◄───┼───┐   │            │nombre   │  │
     │  │(FK)          │   │   │            └─────────┘  │
     │  │nombre        │   │   │                         │
     │  │especie       │   │   └─────────────────────────┘
     │  │raza          │   │
     │  │sexo          │   │
     │  │fecha_nac     │   │
     │  │peso          │   │
     │  │color         │   │
     │  │alergias      │   │
     │  │estado        │   │
     │  │observ        │   │
     │  │microchip(U)  │   │
     │  │esterilizado  │   │
     │  └──────────────┘   │
     │                     │
     │                     │ (1)
     │                     ▼
     │              ┌──────────────┐
     │              │  CLIENTES    │
     │              ├──────────────┤
     │              │id (PK)       │
     │              │nombre        │
     │              │telefono(U)   │
     │              │email         │
     │              │direccion     │
     │              │fecha_reg     │
     │              └──────────────┘
     │
     └─────────────────┬───────────────┐
                       │ (1)           │ (1)
                       ▼               ▼
                  ┌────────────┐  ┌──────────────┐
                  │SIGNOS_VTAL │  │  SEGUIMIENTOS│
                  │    ES      │  │              │
                  ├────────────┤  ├──────────────┤
                  │id          │  │id            │
                  │mascota_id◄─┼──│mascota_id    │
                  │(FK)        │  │(FK)          │
                  │consulta_id◄┼──│consulta_id   │
                  │(FK)        │  │(FK)          │
                  │peso        │  │descripcion   │
                  │temperat    │  │estado        │
                  │freq_card   │  │fecha_prog    │
                  │freq_resp   │  │fecha_real    │
                  │observ      │  │observ        │
                  │fecha_reg   │  └──────────────┘
                  └────────────┘
                       ▲
                       │ (1)
                       │
                       └────────────────────┘
                       
                       CITA (1) ◄────► (1) PAGOS
                                        ├──────────┐
                                        │cita_id(FK)
                                        │cliente_id│
                                        │monto     │
                                        │metodo    │
                                        │estado    │
                                        │fecha     │
                                        │comprobant│
                                        │observ    │
                                        └──────────┘
```

---

## Relaciones Detalladas

### 1. USUARIOS → CONSULTAS
- Un usuario (veterinario) puede tener muchas consultas
- Relación: 1 (veterinario) a M (consultas)

### 2. USUARIOS → CITAS
- Un usuario (veterinario) puede tener muchas citas
- Relación: 1 (veterinario) a M (citas)

### 3. USUARIOS → VACUNAS_APLICADAS
- Un usuario (veterinario) puede aplicar muchas vacunas
- Relación: 1 (veterinario) a M (vacunas aplicadas)

### 4. USUARIOS → AGENDA
- Un usuario (veterinario) tiene su propia agenda
- Relación: 1 (veterinario) a M (horarios de agenda)

### 5. USUARIOS → ALERTAS_CRITICAS
- Un usuario (veterinario) puede resolver muchas alertas
- Relación: 1 (usuario) a M (alertas resueltas por)

### 6. CLIENTES → MASCOTAS
- Un cliente puede tener muchas mascotas
- Relación: 1 (cliente) a M (mascotas)
- Cascade: DELETE

### 7. CLIENTES → PAGOS
- Un cliente puede hacer muchos pagos
- Relación: 1 (cliente) a M (pagos)

### 8. MASCOTAS → CONSULTAS
- Una mascota puede tener muchas consultas
- Relación: 1 (mascota) a M (consultas)
- Cascade: DELETE

### 9. MASCOTAS → CITAS
- Una mascota puede tener muchas citas
- Relación: 1 (mascota) a M (citas)
- Cascade: DELETE

### 10. MASCOTAS → SIGNOS_VITALES
- Una mascota puede tener muchos registros de signos vitales
- Relación: 1 (mascota) a M (signos vitales)
- Cascade: DELETE

### 11. MASCOTAS → SEGUIMIENTOS
- Una mascota puede tener muchos seguimientos
- Relación: 1 (mascota) a M (seguimientos)
- Cascade: DELETE

### 12. MASCOTAS → VACUNAS_APLICADAS
- Una mascota puede recibir muchas vacunas
- Relación: 1 (mascota) a M (vacunas aplicadas)
- Cascade: DELETE

### 13. VACUNAS → VACUNAS_APLICADAS
- Una vacuna puede ser aplicada muchas veces
- Relación: 1 (vacuna) a M (vacunas aplicadas)
- Restrict: No eliminar vacunas si hay aplicaciones

### 14. CONSULTAS → SIGNOS_VITALES
- Una consulta puede tener muchos registros de signos vitales
- Relación: 1 (consulta) a M (signos vitales)

### 15. CONSULTAS → SEGUIMIENTOS
- Una consulta puede tener muchos seguimientos
- Relación: 1 (consulta) a M (seguimientos)

### 16. CONSULTAS → ALERTAS_CRITICAS
- Una consulta puede generar muchas alertas
- Relación: 1 (consulta) a M (alertas críticas)

### 17. CITAS → PAGOS
- Una cita puede tener un pago asociado
- Relación: 1 (cita) a 1 (pago)

---

## Claves Primarias (PK)

Todas las tablas usan `BIGSERIAL` como clave primaria:
```sql
id BIGSERIAL PRIMARY KEY
```

Esto proporciona:
- Auto-incremento automático
- Compatibilidad con JPA/Hibernate
- Soporte para números grandes (hasta 9,223,372,036,854,775,807)

---

## Claves Únicas (UNIQUE)

```
usuarios.username      - Un username por usuario
clientes.telefono      - Un teléfono por cliente
mascotas.microchip     - Un microchip por mascota
```

---

## Índices Creados

Para optimizar las búsquedas frecuentes:

```sql
-- Usuarios
idx_usuarios_username
idx_usuarios_rol
idx_usuarios_activo

-- Clientes
idx_clientes_nombre
idx_clientes_telefono
idx_clientes_email

-- Mascotas
idx_mascotas_cliente_id
idx_mascotas_nombre
idx_mascotas_especie
idx_mascotas_estado
idx_mascotas_microchip

-- Consultas
idx_consultas_mascota_id
idx_consultas_veterinario_id
idx_consultas_fecha
idx_consultas_estado

-- Signos Vitales
idx_signos_vitales_mascota_id
idx_signos_vitales_consulta_id
idx_signos_vitales_fecha

-- Seguimientos
idx_seguimientos_mascota_id
idx_seguimientos_consulta_id
idx_seguimientos_estado
idx_seguimientos_fecha_programada

-- Vacunas Aplicadas
idx_vacunas_aplicadas_mascota_id
idx_vacunas_aplicadas_vacuna_id
idx_vacunas_aplicadas_veterinario_id
idx_vacunas_aplicadas_fecha

-- Alertas Críticas
idx_alertas_criticas_mascota_id
idx_alertas_criticas_consulta_id
idx_alertas_criticas_prioridad
idx_alertas_criticas_estado

-- Citas
idx_citas_mascota_id
idx_citas_veterinario_id
idx_citas_fecha_hora
idx_citas_estado

-- Pagos
idx_pagos_cita_id
idx_pagos_cliente_id
idx_pagos_estado
idx_pagos_fecha

-- Agenda
idx_agenda_veterinario_id
idx_agenda_fecha
idx_agenda_disponible
```

---

## Cascadas (ON DELETE)

```
CLIENTES → MASCOTAS       : CASCADE (si elimino cliente, se eliminan sus mascotas)
MASCOTAS → CITAS          : CASCADE (si elimino mascota, se eliminan sus citas)
MASCOTAS → CONSULTAS      : CASCADE (si elimino mascota, se eliminan sus consultas)
MASCOTAS → SIGNOS_VITALES : CASCADE (si elimino mascota, se eliminan registros)
MASCOTAS → SEGUIMIENTOS   : CASCADE (si elimino mascota, se eliminan seguimientos)
MASCOTAS → VACUNAS_APLIC  : CASCADE (si elimino mascota, se eliminan aplicaciones)
USUARIOS → AGENDA         : CASCADE (si elimino veterinario, se elimina su agenda)

VACUNAS → VACUNAS_APLIC   : RESTRICT (no puedo eliminar si hay aplicaciones)
```

---

## Tipos de Datos PostgreSQL Usados

```
BIGSERIAL          - Enteros de 64 bits auto-incrementables
BOOLEAN            - true/false
VARCHAR(n)         - Strings de longitud máxima n
TEXT               - Strings de longitud variable (sin límite)
DATE               - Fecha (YYYY-MM-DD)
TIME               - Hora (HH:MM:SS)
TIMESTAMP          - Fecha y hora con timestamp
DECIMAL(p, s)      - Decimales con precisión p y escala s
INTEGER            - Enteros de 32 bits
```

---

## Campos de Auditoría

Algunas tablas incluyen:
```sql
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
```

Estos campos permiten rastrear:
- Cuándo se creó un registro
- Cuándo se modificó por última vez

---

## Consultas SQL Comunes

```sql
-- Obtener mascotas de un cliente
SELECT m.* FROM mascotas m
WHERE m.cliente_id = 1;

-- Obtener todas las citas agendadas
SELECT c.*, m.nombre as mascota, v.nombre as veterinario
FROM citas c
JOIN mascotas m ON c.mascota_id = m.id
JOIN usuarios v ON c.veterinario_id = v.id
WHERE c.estado = 'AGENDADA';

-- Obtener consultas realizadas por un veterinario
SELECT c.*, m.nombre as mascota
FROM consultas c
JOIN mascotas m ON c.mascota_id = m.id
WHERE c.veterinario_id = 1;

-- Obtener alertas sin resolver
SELECT a.*, m.nombre as mascota
FROM alertas_criticas a
JOIN mascotas m ON a.mascota_id = m.id
WHERE a.estado != 'RESUELTA';

-- Obtener vacunas aplicadas en el último mes
SELECT va.*, v.nombre as vacuna, m.nombre as mascota
FROM vacunas_aplicadas va
JOIN vacunas v ON va.vacuna_id = v.id
JOIN mascotas m ON va.mascota_id = m.id
WHERE va.fecha_aplicacion >= CURRENT_DATE - INTERVAL '30 days';

-- Obtener total de pagos por cliente
SELECT c.id, c.nombre, COUNT(p.id) as total_pagos, SUM(p.monto) as total_monto
FROM clientes c
LEFT JOIN pagos p ON c.id = p.cliente_id
GROUP BY c.id, c.nombre;
```

---

## Normalización

La base de datos está normalizada en **3NF (Third Normal Form)**:

✅ **1NF**: Todos los atributos contienen valores atómicos
✅ **2NF**: Eliminación de dependencias parciales
✅ **3NF**: Eliminación de dependencias transitivas

Esto garantiza:
- Integridad referencial
- Eficiencia en almacenamiento
- Facilidad de mantenimiento
- Evita anomalías de actualización

---

**Diagrama de la Base de Datos - Veterinaria Pet Clinic Completado** ✅
