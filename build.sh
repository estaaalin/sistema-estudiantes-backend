#!/bin/bash
echo "🔨 Iniciando build personalizado para Railway..."

# Limpiar y compilar
echo "📦 Limpiando proyecto..."
./mvnw clean

echo "🏗️ Compilando proyecto..."
./mvnw package -DskipTests

echo "✅ Build completado!"
echo "📁 Contenido del directorio target:"
ls -la target/

echo "🚀 Archivo JAR listo para despliegue!"
