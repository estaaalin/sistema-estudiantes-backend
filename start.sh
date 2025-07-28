#!/bin/bash

# Script de inicio para Railway
echo "🚀 Iniciando aplicación Spring Boot en Railway..."

# Verificar variables de entorno
echo "PORT: $PORT"
echo "DATABASE_URL: ${DATABASE_URL:No establecida}"

# Ejecutar la aplicación con perfil de producción
exec java -Dspring.profiles.active=prod -Dserver.port=$PORT -jar target/backend-0.0.1-SNAPSHOT.jar
