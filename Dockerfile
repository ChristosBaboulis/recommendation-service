FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY target/Recommendation-Service-0.0.1-SNAPSHOT.jar app.jar
COPY data ./data

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
