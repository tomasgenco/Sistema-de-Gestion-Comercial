# Etapa de build (Java 21)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests

# Etapa de runtime (Java 21)
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/target/*.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
