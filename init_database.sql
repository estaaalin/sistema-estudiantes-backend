-- Script de inicialización completo para la base de datos

-- Crear la tabla estudiantes si no existe
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

-- Eliminar restricciones existentes si hay conflictos
ALTER TABLE estudiantes DROP CONSTRAINT IF EXISTS estudiantes_estado_check;
ALTER TABLE estudiantes DROP CONSTRAINT IF EXISTS estudiantes_rol_check;

-- Crear las restricciones correctas
ALTER TABLE estudiantes ADD CONSTRAINT estudiantes_estado_check 
CHECK (estado IN ('ACTIVO', 'INACTIVO'));

ALTER TABLE estudiantes ADD CONSTRAINT estudiantes_rol_check 
CHECK (rol IN ('ADMINISTRADOR', 'ESTUDIANTE'));

-- Limpiar datos inválidos
UPDATE estudiantes SET estado = 'ACTIVO' WHERE estado NOT IN ('ACTIVO', 'INACTIVO');
UPDATE estudiantes SET rol = 'ESTUDIANTE' WHERE rol NOT IN ('ADMINISTRADOR', 'ESTUDIANTE');

-- Insertar usuario administrador por defecto
INSERT INTO estudiantes (nombre_completo, correo, telefono, fecha_nacimiento, carrera, estado, password, rol) 
VALUES ('Administrador Sistema', 'admin@universidad.edu', '1234567890', '1990-01-01', 'Administración', 'ACTIVO', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ADMINISTRADOR')
ON CONFLICT (correo) DO NOTHING;
