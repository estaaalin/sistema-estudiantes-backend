@echo off
echo ========================================
echo    SUITE DE PRUEBAS AUTOMATIZADAS
echo         BACKEND F1NEWS
echo ========================================
echo.

echo Ejecutando todas las pruebas automatizadas...
echo.

REM Ejecutar pruebas de autenticación
echo [1/3] Ejecutando Pruebas de Autenticación (Login)...
call mvnw.cmd test -Dtest=SimpleAuthTest -q
if %ERRORLEVEL% neq 0 (
    echo ERROR: Pruebas de autenticación fallaron
    exit /b 1
)
echo ✓ Pruebas de autenticación: PASARON
echo.

REM Ejecutar pruebas de validación
echo [2/3] Ejecutando Pruebas de Validación...
call mvnw.cmd test -Dtest=ValidationTest -q
if %ERRORLEVEL% neq 0 (
    echo ERROR: Pruebas de validación fallaron
    exit /b 1
)
echo ✓ Pruebas de validación: PASARON
echo.

REM Ejecutar pruebas CRUD
echo [3/3] Ejecutando Pruebas CRUD...
call mvnw.cmd test -Dtest=CrudOperationsTest -q
if %ERRORLEVEL% neq 0 (
    echo ERROR: Pruebas CRUD fallaron
    exit /b 1
)
echo ✓ Pruebas CRUD: PASARON
echo.

echo ========================================
echo    TODAS LAS PRUEBAS COMPLETADAS
echo ========================================
echo.
echo Resumen de pruebas:
echo - Autenticación: 5 pruebas (Login estudiante/admin, credenciales incorrectas, usuario inexistente, roles)
echo - Validación: 5 pruebas (Correo duplicado, formato correo, campos requeridos, actualización)
echo - CRUD: 10 pruebas (Crear, leer, actualizar, eliminar estudiantes y administradores)
echo.
echo Total: 20 pruebas automatizadas ejecutadas exitosamente
echo Estado: ✓ TODAS LAS PRUEBAS PASARON
echo.
pause
