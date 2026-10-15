# Etapa 1: compilar con Maven
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY maven-settings.xml /root/.m2/settings.xml
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: imagen final, liviana, solo con el jar
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
