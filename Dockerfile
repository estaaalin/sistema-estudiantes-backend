# Usar imagen base de OpenJDK 17
FROM openjdk:17-jdk-slim

# Establecer directorio de trabajo
WORKDIR /app

# Copiar el archivo JAR
COPY target/backend-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto
EXPOSE $PORT

# Comando de inicio
CMD ["java", "-Dspring.profiles.active=prod", "-Dserver.port=${PORT}", "-jar", "app.jar"]
