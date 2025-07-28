-- Script para crear datos iniciales en la base de datos

-- Crear usuario administrador por defecto
-- Contraseña: admin123 (debe ser encriptada por la aplicación)
INSERT INTO estudiantes (nombre_completo, correo, telefono, fecha_nacimiento, carrera, estado, password, rol) 
VALUES ('Administrador Sistema', 'admin@universidad.edu', '1234567890', '1990-01-01', 'Administración', 'ACTIVO', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ADMINISTRADOR')
ON CONFLICT (correo) DO NOTHING;

-- Crear algunos estudiantes de ejemplo
-- Contraseña para todos: student123 (debe ser encriptada por la aplicación)
INSERT INTO estudiantes (nombre_completo, correo, telefono, fecha_nacimiento, carrera, estado, password, rol) 
VALUES 
('Juan Carlos Pérez', 'juan.perez@estudiante.edu', '9876543210', '2000-03-15', 'Ingeniería de Sistemas', 'ACTIVO', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ESTUDIANTE'),
('María González López', 'maria.gonzalez@estudiante.edu', '8765432109', '1999-07-22', 'Ingeniería Industrial', 'ACTIVO', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ESTUDIANTE'),
('Carlos Alberto Ruiz', 'carlos.ruiz@estudiante.edu', '7654321098', '2001-11-08', 'Medicina', 'ACTIVO', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ESTUDIANTE'),
('Ana Sofía Martínez', 'ana.martinez@estudiante.edu', '6543210987', '2000-05-30', 'Derecho', 'ACTIVO', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ESTUDIANTE'),
('Pedro Pablo Jiménez', 'pedro.jimenez@estudiante.edu', '5432109876', '1998-12-12', 'Arquitectura', 'INACTIVO', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ESTUDIANTE')
ON CONFLICT (correo) DO NOTHING;
