-- Script de inicialización para Railway
-- Este script se ejecutará automáticamente en Railway

-- Crear tabla administradores
CREATE TABLE IF NOT EXISTS administradores (
    id BIGSERIAL PRIMARY KEY,
    nombre_completo VARCHAR(255) NOT NULL,
    correo VARCHAR(255) UNIQUE NOT NULL,
    telefono VARCHAR(255),
    fecha_nacimiento DATE,
    estado VARCHAR(50) NOT NULL DEFAULT 'ACTIVO',
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(50) NOT NULL DEFAULT 'ADMINISTRADOR',
    foto_url TEXT
);

-- Crear restricciones
ALTER TABLE administradores DROP CONSTRAINT IF EXISTS administradores_estado_check;
ALTER TABLE administradores DROP CONSTRAINT IF EXISTS administradores_rol_check;

ALTER TABLE administradores ADD CONSTRAINT administradores_estado_check 
CHECK (estado IN ('ACTIVO', 'INACTIVO'));

ALTER TABLE administradores ADD CONSTRAINT administradores_rol_check 
CHECK (rol IN ('ADMINISTRADOR', 'SUPER_ADMIN'));

-- Crear tabla estudiantes
CREATE TABLE IF NOT EXISTS estudiantes (
    id BIGSERIAL PRIMARY KEY,
    nombre_completo VARCHAR(255) NOT NULL,
    correo VARCHAR(255) UNIQUE NOT NULL,
    telefono VARCHAR(255) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    carrera VARCHAR(255) NOT NULL,
    estado VARCHAR(50) NOT NULL DEFAULT 'ACTIVO',
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(50) NOT NULL DEFAULT 'ESTUDIANTE'
);

-- Crear restricciones para estudiantes
ALTER TABLE estudiantes DROP CONSTRAINT IF EXISTS estudiantes_estado_check;
ALTER TABLE estudiantes DROP CONSTRAINT IF EXISTS estudiantes_rol_check;

ALTER TABLE estudiantes ADD CONSTRAINT estudiantes_estado_check 
CHECK (estado IN ('ACTIVO', 'INACTIVO'));

ALTER TABLE estudiantes ADD CONSTRAINT estudiantes_rol_check 
CHECK (rol IN ('ADMINISTRADOR', 'ESTUDIANTE'));

-- Insertar administrador por defecto (password: admin123)
INSERT INTO administradores (nombre_completo, correo, telefono, fecha_nacimiento, estado, password, rol) 
VALUES (
    'Administrador Sistema', 
    'admin@sistema.com', 
    '1234567890', 
    '1990-01-01', 
    'ACTIVO', 
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 
    'ADMINISTRADOR'
)
ON CONFLICT (correo) DO NOTHING;

-- Insertar estudiante de prueba (password: student123)
INSERT INTO estudiantes (nombre_completo, correo, telefono, fecha_nacimiento, carrera, estado, password, rol) 
VALUES (
    'Estudiante Prueba', 
    'estudiante@test.com', 
    '0987654321', 
    '2000-05-15', 
    'Ingeniería de Sistemas', 
    'ACTIVO', 
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 
    'ESTUDIANTE'
)
ON CONFLICT (correo) DO NOTHING;
