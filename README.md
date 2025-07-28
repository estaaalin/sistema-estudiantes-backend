# Sistema de Gestión de Estudiantes - Backend

Este es el backend de un sistema completo para la gestión de estudiantes con autenticación JWT y control de roles.

## 🏗️ Arquitectura del Proyecto

El proyecto está desarrollado con **Spring Boot 3.2.0** y utiliza las siguientes tecnologías:

- **Spring Boot** - Framework principal
- **Spring Security** - Autenticación y autorización
- **Spring Data JPA** - Persistencia de datos
- **PostgreSQL** - Base de datos
- **JWT (JSON Web Tokens)** - Autenticación stateless
- **BCrypt** - Encriptación de contraseñas
- **Maven** - Gestión de dependencias
- **JUnit 5** - Pruebas unitarias

## 👥 Roles del Sistema

### 🔐 ADMINISTRADOR
- Puede iniciar sesión
- Crear nuevos estudiantes
- Listar todos los estudiantes
- Editar cualquier estudiante
- Eliminar cualquier estudiante
- Filtrar estudiantes por estado y carrera
- Buscar estudiantes por nombre

### 📚 ESTUDIANTE
- Puede iniciar sesión
- Ver su propio perfil
- Editar su propia información
- **NO** puede acceder a información de otros estudiantes

## 📋 Modelo de Datos

### Estudiante
```java
- id: Long (Identificador único)
- nombreCompleto: String (Nombre completo del estudiante)
- correo: String (Correo único para autenticación)
- telefono: String (Número de teléfono)
- fechaNacimiento: LocalDate (Fecha de nacimiento)
- carrera: String (Carrera que estudia)
- estado: EstadoEstudiante (ACTIVO/INACTIVO)
- password: String (Contraseña encriptada)
- rol: Rol (ADMINISTRADOR/ESTUDIANTE)
```

## 🛠️ Configuración del Proyecto

### Prerrequisitos
- Java 17 o superior
- Maven 3.6 o superior
- PostgreSQL 12 o superior

### Configuración de Base de Datos

1. Crear una base de datos PostgreSQL:
```sql
CREATE DATABASE estudiantes_db;
```

2. Configurar las credenciales en `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/estudiantes_db
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
```

### Instalación y Ejecución

1. Clonar el proyecto
2. Configurar la base de datos
3. Ejecutar el proyecto:
```bash
mvn spring-boot:run
```

El servidor se ejecutará en: `http://localhost:8080`

## 🔗 Endpoints de la API

### 🔐 Autenticación

#### POST /auth/login
Permite el inicio de sesión tanto para administradores como estudiantes.

**Request:**
```json
{
  "correo": "usuario@email.com",
  "password": "contraseña"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "correo": "usuario@email.com",
  "nombreCompleto": "Nombre Usuario",
  "rol": "ADMINISTRADOR"
}
```

### 👨‍🎓 Endpoints para Administrador

#### GET /api/estudiantes
Obtener lista completa de estudiantes (Solo ADMINISTRADOR)

#### POST /api/estudiantes
Crear un nuevo estudiante (Solo ADMINISTRADOR)

**Request:**
```json
{
  "nombreCompleto": "Juan Pérez",
  "correo": "juan@estudiante.edu",
  "telefono": "1234567890",
  "fechaNacimiento": "2000-01-15",
  "carrera": "Ingeniería de Sistemas",
  "password": "contraseña123"
}
```

#### PUT /api/estudiantes/{id}
Editar cualquier estudiante (Solo ADMINISTRADOR)

#### DELETE /api/estudiantes/{id}
Eliminar un estudiante (Solo ADMINISTRADOR)

#### GET /api/estudiantes/estado/{estado}
Filtrar estudiantes por estado: ACTIVO/INACTIVO (Solo ADMINISTRADOR)

#### GET /api/estudiantes/carrera/{carrera}
Filtrar estudiantes por carrera (Solo ADMINISTRADOR)

#### GET /api/estudiantes/buscar?nombre={nombre}
Buscar estudiantes por nombre (Solo ADMINISTRADOR)

### 🎓 Endpoints para Estudiante

#### GET /api/estudiantes/{id}
Ver perfil propio (Estudiante solo puede ver su ID)

#### PUT /api/estudiantes/{id}
Actualizar información propia (Estudiante solo puede editar su ID)

## 🔒 Seguridad

### JWT (JSON Web Tokens)
- Los tokens tienen una validez de 24 horas
- Contienen información del usuario: ID, correo y rol
- Se envían en el header Authorization: `Bearer {token}`

### Middleware de Autenticación
- Verifica la validez del token JWT
- Extrae la información del usuario
- Valida los permisos según el rol

### Encriptación
- Las contraseñas se encriptan con BCrypt
- Salt rounds: 10 (por defecto de Spring Security)

## 🧪 Pruebas

El proyecto incluye pruebas unitarias para:
- Servicios (EstudianteService)
- Controladores (AuthController)
- Repositorios

Ejecutar pruebas:
```bash
mvn test
```

## 📊 Datos de Prueba

El archivo `fix_noticia_sequence.sql` contiene datos iniciales:

### Usuario Administrador
- **Correo:** admin@universidad.edu
- **Contraseña:** admin123

### Estudiantes de Ejemplo
- **Correo:** juan.perez@estudiante.edu
- **Contraseña:** student123

## 🚀 Despliegue

### Variables de Entorno Recomendadas
```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://host:port/database
SPRING_DATASOURCE_USERNAME=username
SPRING_DATASOURCE_PASSWORD=password
JWT_SECRET=tu-secreto-jwt-super-seguro
JWT_EXPIRATION=86400000
```

### Profile de Producción
Crear `application-prod.properties` para configuraciones específicas de producción.

## 📁 Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/f1news/backend/
│   │   ├── config/          # Configuraciones (Security, JWT)
│   │   ├── controller/      # Controladores REST
│   │   ├── model/          # Entidades JPA
│   │   ├── repository/     # Repositorios JPA
│   │   ├── service/        # Lógica de negocio
│   │   └── F1newsBackendApplication.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/f1news/backend/
        ├── EstudianteServiceTest.java
        └── AuthControllerTest.java
```

## 🔧 Tecnologías Utilizadas

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security 6**
- **Spring Data JPA**
- **PostgreSQL**
- **JWT (jjwt 0.11.5)**
- **Maven**
- **JUnit 5**
- **Mockito**

## 📞 Soporte

Para soporte técnico o consultas sobre el proyecto, contactar al equipo de desarrollo.

---

**Desarrollado con ❤️ usando Spring Boot**
