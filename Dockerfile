FROM eclipse-temurin:21.0.2_13-jdk

COPY build/libs/rinha-2024-q1-vertx-0.0.1-fat.jar app.jar

CMD ["java", "-XX:MaxRAMPercentage=80.0", "-jar", "app.jar"]
