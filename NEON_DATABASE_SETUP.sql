-- ============================================================
-- Script SQL para Neon Database - Veterinaria Pet Clinic
-- ============================================================
-- Este script crea todas las tablas necesarias para que el 
-- proyecto funcione correctamente con Neon PostgreSQL
-- Connection String: postgresql://neondb_owner:npg_niRekK7UZ9mD@ep-cool-shape-aqja9v6c-pooler.c-8.us-east-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require
-- ============================================================

-- Limpiar tablas existentes (CUIDADO: esto elimina todos los datos)
-- DROP TABLE IF EXISTS vacunas_aplicadas CASCADE;
-- DROP TABLE IF EXISTS alertas_criticas CASCADE;
-- DROP TABLE IF EXISTS signos_vitales CASCADE;
-- DROP TABLE IF EXISTS seguimientos CASCADE;
-- DROP TABLE IF EXISTS pagos CASCADE;
-- DROP TABLE IF EXISTS citas CASCADE;
-- DROP TABLE IF EXISTS consultas CASCADE;
-- DROP TABLE IF EXISTS mascotas CASCADE;
-- DROP TABLE IF EXISTS agenda CASCADE;
-- DROP TABLE IF EXISTS vacunas CASCADE;
-- DROP TABLE IF EXISTS usuarios CASCADE;
-- DROP TABLE IF EXISTS clientes CASCADE;

-- ============================================================
-- 1. Tabla USUARIOS
-- ============================================================
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    rol VARCHAR(60) NOT NULL,
    activo BOOLEAN DEFAULT true,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para usuarios
CREATE INDEX idx_usuarios_username ON usuarios(username);
CREATE INDEX idx_usuarios_rol ON usuarios(rol);
CREATE INDEX idx_usuarios_activo ON usuarios(activo);

-- ============================================================
-- 2. Tabla CLIENTES
-- ============================================================
CREATE TABLE IF NOT EXISTS clientes (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(100),
    direccion VARCHAR(200),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para clientes
CREATE INDEX idx_clientes_nombre ON clientes(nombre);
CREATE INDEX idx_clientes_telefono ON clientes(telefono);
CREATE INDEX idx_clientes_email ON clientes(email);

-- ============================================================
-- 3. Tabla VACUNAS
-- ============================================================
CREATE TABLE IF NOT EXISTS vacunas (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para vacunas
CREATE INDEX idx_vacunas_nombre ON vacunas(nombre);

-- ============================================================
-- 4. Tabla MASCOTAS
-- ============================================================
CREATE TABLE IF NOT EXISTS mascotas (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    especie VARCHAR(50) NOT NULL,
    raza VARCHAR(50),
    sexo VARCHAR(20),
    fecha_nacimiento DATE,
    peso DECIMAL(10, 2),
    color VARCHAR(50),
    alergias TEXT,
    estado VARCHAR(50),
    observaciones TEXT,
    microchip VARCHAR(50) UNIQUE,
    esterilizado BOOLEAN DEFAULT false,
    foto_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_mascotas_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE CASCADE
);

-- Índices para mascotas
CREATE INDEX idx_mascotas_cliente_id ON mascotas(cliente_id);
CREATE INDEX idx_mascotas_nombre ON mascotas(nombre);
CREATE INDEX idx_mascotas_especie ON mascotas(especie);
CREATE INDEX idx_mascotas_estado ON mascotas(estado);
CREATE INDEX idx_mascotas_microchip ON mascotas(microchip);

-- ============================================================
-- 5. Tabla CONSULTAS
-- ============================================================
CREATE TABLE IF NOT EXISTS consultas (
    id BIGSERIAL PRIMARY KEY,
    mascota_id BIGINT NOT NULL,
    veterinario_id BIGINT,
    fecha_consulta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    motivo_consulta TEXT,
    observaciones TEXT,
    estado VARCHAR(50),
    peso DECIMAL(10, 2),
    temperatura DECIMAL(10, 2),
    frecuencia_cardiaca INTEGER,
    frecuencia_respiratoria INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_consultas_mascota FOREIGN KEY (mascota_id) REFERENCES mascotas(id) ON DELETE CASCADE,
    CONSTRAINT fk_consultas_veterinario FOREIGN KEY (veterinario_id) REFERENCES usuarios(id) ON DELETE SET NULL
);

-- Índices para consultas
CREATE INDEX idx_consultas_mascota_id ON consultas(mascota_id);
CREATE INDEX idx_consultas_veterinario_id ON consultas(veterinario_id);
CREATE INDEX idx_consultas_fecha ON consultas(fecha_consulta);
CREATE INDEX idx_consultas_estado ON consultas(estado);

-- ============================================================
-- 6. Tabla SIGNOS_VITALES
-- ============================================================
CREATE TABLE IF NOT EXISTS signos_vitales (
    id BIGSERIAL PRIMARY KEY,
    mascota_id BIGINT NOT NULL,
    consulta_id BIGINT,
    peso DECIMAL(10, 2),
    temperatura DECIMAL(10, 2),
    frecuencia_cardiaca INTEGER,
    frecuencia_respiratoria INTEGER,
    observaciones TEXT,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_signos_vitales_mascota FOREIGN KEY (mascota_id) REFERENCES mascotas(id) ON DELETE CASCADE,
    CONSTRAINT fk_signos_vitales_consulta FOREIGN KEY (consulta_id) REFERENCES consultas(id) ON DELETE SET NULL
);

-- Índices para signos_vitales
CREATE INDEX idx_signos_vitales_mascota_id ON signos_vitales(mascota_id);
CREATE INDEX idx_signos_vitales_consulta_id ON signos_vitales(consulta_id);
CREATE INDEX idx_signos_vitales_fecha ON signos_vitales(fecha_registro);

-- ============================================================
-- 7. Tabla SEGUIMIENTOS
-- ============================================================
CREATE TABLE IF NOT EXISTS seguimientos (
    id BIGSERIAL PRIMARY KEY,
    mascota_id BIGINT NOT NULL,
    consulta_id BIGINT,
    descripcion VARCHAR(500),
    estado VARCHAR(50),
    fecha_programada DATE,
    fecha_realizada DATE,
    observaciones TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_seguimientos_mascota FOREIGN KEY (mascota_id) REFERENCES mascotas(id) ON DELETE CASCADE,
    CONSTRAINT fk_seguimientos_consulta FOREIGN KEY (consulta_id) REFERENCES consultas(id) ON DELETE SET NULL
);

-- Índices para seguimientos
CREATE INDEX idx_seguimientos_mascota_id ON seguimientos(mascota_id);
CREATE INDEX idx_seguimientos_consulta_id ON seguimientos(consulta_id);
CREATE INDEX idx_seguimientos_estado ON seguimientos(estado);
CREATE INDEX idx_seguimientos_fecha_programada ON seguimientos(fecha_programada);

-- ============================================================
-- 8. Tabla VACUNAS_APLICADAS
-- ============================================================
CREATE TABLE IF NOT EXISTS vacunas_aplicadas (
    id BIGSERIAL PRIMARY KEY,
    mascota_id BIGINT NOT NULL,
    vacuna_id BIGINT NOT NULL,
    veterinario_id BIGINT,
    fecha_aplicacion DATE,
    proxima_dosis DATE,
    observaciones TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vacunas_aplicadas_mascota FOREIGN KEY (mascota_id) REFERENCES mascotas(id) ON DELETE CASCADE,
    CONSTRAINT fk_vacunas_aplicadas_vacuna FOREIGN KEY (vacuna_id) REFERENCES vacunas(id) ON DELETE RESTRICT,
    CONSTRAINT fk_vacunas_aplicadas_veterinario FOREIGN KEY (veterinario_id) REFERENCES usuarios(id) ON DELETE SET NULL
);

-- Índices para vacunas_aplicadas
CREATE INDEX idx_vacunas_aplicadas_mascota_id ON vacunas_aplicadas(mascota_id);
CREATE INDEX idx_vacunas_aplicadas_vacuna_id ON vacunas_aplicadas(vacuna_id);
CREATE INDEX idx_vacunas_aplicadas_veterinario_id ON vacunas_aplicadas(veterinario_id);
CREATE INDEX idx_vacunas_aplicadas_fecha ON vacunas_aplicadas(fecha_aplicacion);

-- ============================================================
-- 9. Tabla ALERTAS_CRITICAS
-- ============================================================
CREATE TABLE IF NOT EXISTS alertas_criticas (
    id BIGSERIAL PRIMARY KEY,
    mascota_id BIGINT NOT NULL,
    consulta_id BIGINT,
    tipo_alerta VARCHAR(100),
    descripcion VARCHAR(500),
    prioridad VARCHAR(50),
    estado VARCHAR(50),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resuelta_por BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_alertas_criticas_mascota FOREIGN KEY (mascota_id) REFERENCES mascotas(id) ON DELETE CASCADE,
    CONSTRAINT fk_alertas_criticas_consulta FOREIGN KEY (consulta_id) REFERENCES consultas(id) ON DELETE SET NULL,
    CONSTRAINT fk_alertas_criticas_usuario FOREIGN KEY (resuelta_por) REFERENCES usuarios(id) ON DELETE SET NULL
);

-- Índices para alertas_criticas
CREATE INDEX idx_alertas_criticas_mascota_id ON alertas_criticas(mascota_id);
CREATE INDEX idx_alertas_criticas_consulta_id ON alertas_criticas(consulta_id);
CREATE INDEX idx_alertas_criticas_prioridad ON alertas_criticas(prioridad);
CREATE INDEX idx_alertas_criticas_estado ON alertas_criticas(estado);

-- ============================================================
-- 10. Tabla CITAS
-- ============================================================
CREATE TABLE IF NOT EXISTS citas (
    id BIGSERIAL PRIMARY KEY,
    mascota_id BIGINT NOT NULL,
    veterinario_id BIGINT,
    fecha_hora TIMESTAMP NOT NULL,
    motivo VARCHAR(200),
    estado VARCHAR(50) DEFAULT 'AGENDADA',
    observaciones TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_citas_mascota FOREIGN KEY (mascota_id) REFERENCES mascotas(id) ON DELETE CASCADE,
    CONSTRAINT fk_citas_veterinario FOREIGN KEY (veterinario_id) REFERENCES usuarios(id) ON DELETE SET NULL
);

-- Índices para citas
CREATE INDEX idx_citas_mascota_id ON citas(mascota_id);
CREATE INDEX idx_citas_veterinario_id ON citas(veterinario_id);
CREATE INDEX idx_citas_fecha_hora ON citas(fecha_hora);
CREATE INDEX idx_citas_estado ON citas(estado);

-- ============================================================
-- 11. Tabla PAGOS
-- ============================================================
CREATE TABLE IF NOT EXISTS pagos (
    id BIGSERIAL PRIMARY KEY,
    cita_id BIGINT,
    cliente_id BIGINT,
    monto DECIMAL(10, 2),
    metodo_pago VARCHAR(50),
    estado VARCHAR(50) DEFAULT 'PENDIENTE',
    fecha_pago TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    comprobante VARCHAR(500),
    observaciones TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pagos_cita FOREIGN KEY (cita_id) REFERENCES citas(id) ON DELETE SET NULL,
    CONSTRAINT fk_pagos_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE SET NULL
);

-- Índices para pagos
CREATE INDEX idx_pagos_cita_id ON pagos(cita_id);
CREATE INDEX idx_pagos_cliente_id ON pagos(cliente_id);
CREATE INDEX idx_pagos_estado ON pagos(estado);
CREATE INDEX idx_pagos_fecha ON pagos(fecha_pago);

-- ============================================================
-- 12. Tabla AGENDA
-- ============================================================
CREATE TABLE IF NOT EXISTS agenda (
    id BIGSERIAL PRIMARY KEY,
    veterinario_id BIGINT NOT NULL,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    duracion_turno INTEGER,
    disponible BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_agenda_veterinario FOREIGN KEY (veterinario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Índices para agenda
CREATE INDEX idx_agenda_veterinario_id ON agenda(veterinario_id);
CREATE INDEX idx_agenda_fecha ON agenda(fecha);
CREATE INDEX idx_agenda_disponible ON agenda(disponible);

-- ============================================================
-- DATOS INICIALES (Usuarios de ejemplo)
-- ============================================================

-- Insertar usuarios iniciales
-- Contraseña encriptada: password123
-- Hash BCrypt verificado: $2a$10$kSJm.r6x3VAPMWQzRPJ29e2MFKAu5pPR0BqhDqaClU7R1RN8IYrPW
INSERT INTO usuarios (username, password, nombre, email, rol, activo) VALUES
('admin', '$2a$10$kSJm.r6x3VAPMWQzRPJ29e2MFKAu5pPR0BqhDqaClU7R1RN8IYrPW', 'Administrador', 'admin@vetclinic.com', 'ADMIN', true)
ON CONFLICT (username) DO NOTHING;

-- Insertar veterinarios
INSERT INTO usuarios (username, password, nombre, email, rol, activo) VALUES
('vet_juan', '$2a$10$kSJm.r6x3VAPMWQzRPJ29e2MFKAu5pPR0BqhDqaClU7R1RN8IYrPW', 'Juan García', 'juan@vetclinic.com', 'VETERINARIO', true),
('vet_maria', '$2a$10$kSJm.r6x3VAPMWQzRPJ29e2MFKAu5pPR0BqhDqaClU7R1RN8IYrPW', 'María López', 'maria@vetclinic.com', 'VETERINARIO', true)
ON CONFLICT (username) DO NOTHING;

-- Insertar recepcionistas
INSERT INTO usuarios (username, password, nombre, email, rol, activo) VALUES
('recv_carlos', '$2a$10$kSJm.r6x3VAPMWQzRPJ29e2MFKAu5pPR0BqhDqaClU7R1RN8IYrPW', 'Carlos Rodríguez', 'carlos@vetclinic.com', 'RECEPCIONISTA', true),
('recv_ana', '$2a$10$kSJm.r6x3VAPMWQzRPJ29e2MFKAu5pPR0BqhDqaClU7R1RN8IYrPW', 'Ana Martínez', 'ana@vetclinic.com', 'RECEPCIONISTA', true)
ON CONFLICT (username) DO NOTHING;

-- Insertar algunas vacunas predeterminadas
INSERT INTO vacunas (nombre) VALUES
('Rabia'),
('Parvovirus'),
('Distemper'),
('Leucemia Felina'),
('Calicivirus'),
('Herpesvirus Felino')
ON CONFLICT DO NOTHING;

-- ============================================================
-- Fin del script de configuración
-- ============================================================
-- NOTA IMPORTANTE: Las contraseñas están encriptadas con BCrypt
-- Hash BCrypt para "password123": $2a$10$kSJm.r6x3VAPMWQzRPJ29e2MFKAu5pPR0BqhDqaClU7R1RN8IYrPW
-- 
-- Usuarios de prueba:
-- Usuario: admin, Contraseña: password123
-- Usuario: vet_juan, Contraseña: password123
-- Usuario: vet_maria, Contraseña: password123
-- Usuario: recv_carlos, Contraseña: password123
-- Usuario: recv_ana, Contraseña: password123
