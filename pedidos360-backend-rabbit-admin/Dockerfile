FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY maven-settings.xml /root/.m2/settings.xml
COPY src ./src
RUN mvn -q -DskipTests package
FROM eclipse-temurin:21-jre
COPY --from=build /app/target/*.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
