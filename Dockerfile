FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/*.jar app.jar

# Render inyecta la variable PORT
EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
