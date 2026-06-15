# Étape 1 : Build de l'application avec Maven et Java 25
FROM maven:3-eclipse-temurin-25 AS build
COPY . .
RUN mvn clean package -DskipTests

# Étape 2 : Exécution de l'application avec Java 25
FROM eclipse-temurin:25-alpine
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
