# syntax=docker/dockerfile:1

FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copy parent + modules needed for dependency resolution
COPY pom.xml ./pom.xml
COPY common/pom.xml ./common/pom.xml
COPY services/auth-service/pom.xml ./services/auth-service/pom.xml

# Go offline (faster rebuilds)
RUN mvn -q -pl services/auth-service -am dependency:go-offline

# Copy sources
COPY common/src ./common/src
COPY services/auth-service/src ./services/auth-service/src

RUN mvn -q -pl services/auth-service -am -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/services/auth-service/target/*.jar /app/app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]
