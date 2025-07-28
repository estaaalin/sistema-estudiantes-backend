-- Script para arreglar las restricciones de la base de datos

-- Eliminar la restricción problemática si existe
ALTER TABLE estudiantes DROP CONSTRAINT IF EXISTS estudiantes_estado_check;

-- Eliminar la restricción de rol si existe
ALTER TABLE estudiantes DROP CONSTRAINT IF EXISTS estudiantes_rol_check;

-- Recrear las restricciones con los valores correctos
ALTER TABLE estudiantes ADD CONSTRAINT estudiantes_estado_check 
CHECK (estado IN ('ACTIVO', 'INACTIVO'));

ALTER TABLE estudiantes ADD CONSTRAINT estudiantes_rol_check 
CHECK (rol IN ('ADMINISTRADOR', 'ESTUDIANTE'));

-- Actualizar cualquier valor inválido existente
UPDATE estudiantes SET estado = 'ACTIVO' WHERE estado NOT IN ('ACTIVO', 'INACTIVO');
UPDATE estudiantes SET rol = 'ESTUDIANTE' WHERE rol NOT IN ('ADMINISTRADOR', 'ESTUDIANTE');
