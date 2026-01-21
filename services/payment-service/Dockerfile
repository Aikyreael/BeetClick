FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /app

# Copier directement les fichiers présents dans le service
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B package -DskipTests

FROM eclipse-temurin:25-jre

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
