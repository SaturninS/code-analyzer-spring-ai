# Stage 1 : Build (exécuté sur la machine hôte avec le cache Maven local)
# Pour builder l'image : ./mvnw package -DskipTests && docker build -t code-analyzer .

# Stage 2 : Runtime (image légère)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
