# Usa Java 21 súper ligero
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copia el .jar compilado
COPY target/*.jar app.jar

# Expone el puerto 8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]