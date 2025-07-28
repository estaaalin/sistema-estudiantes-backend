# 🚀 Backend - Sistema de Gestión de Estudiantes

## Despliegue en Railway

### Pre-requisitos
- Cuenta en [Railway](https://railway.app)
- Repositorio Git (GitHub, GitLab, etc.)

### Pasos para desplegar:

1. **Subir código a Git**:
```bash
git add .
git commit -m "Preparar para despliegue en Railway"
git push origin main
```

2. **En Railway**:
   - Ir a [railway.app](https://railway.app)
   - Hacer login con GitHub
   - Crear nuevo proyecto
   - Conectar repositorio
   - Railway detectará automáticamente que es un proyecto Spring Boot

3. **Configurar variables de entorno**:
   - `DATABASE_URL`: Railway lo proporciona automáticamente
   - `JWT_SECRET`: clave secreta para JWT
   - `FRONTEND_URL`: URL del frontend desplegado

### Variables de entorno importantes:
```
PORT=8080 (Railway lo establece automáticamente)
DATABASE_URL=postgresql://... (Railway lo proporciona)
JWT_SECRET=tu_clave_secreta_jwt
FRONTEND_URL=https://tu-frontend.vercel.app
SPRING_PROFILES_ACTIVE=prod
```

### Endpoints principales:
- `POST /auth/login` - Login de usuarios
- `GET /api/administradores` - Listar administradores
- `PUT /api/administradores/{id}` - Actualizar administrador
- `GET /api/estudiantes` - Listar estudiantes

### Credenciales por defecto:
- **Admin**: admin@sistema.com / admin123
- **Estudiante**: estudiante@test.com / student123

### Troubleshooting:
- Si hay problemas con CORS, verificar `FRONTEND_URL`
- Si hay problemas con BD, Railway proporciona PostgreSQL automáticamente
- Los logs están disponibles en el dashboard de Railway
