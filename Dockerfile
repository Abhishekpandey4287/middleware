# FROM maven:3.9.8-eclipse-temurin-21 AS build
# COPY . .
# RUN mvn clean package -DskipTests
#
# FROM arm64v8/openjdk:21-ea-21-jdk-slim
# COPY --from=build /target/Social_Media-0.0.1-SNAPSHOT.jar  Social_Media.jar
# EXPOSE 8080
# ENTRYPOINT ["java","-jar","Social_Media.jar"]

# ---------- Stage 1: Build ----------
FROM maven:3.9.8-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests


# ---------- Stage 2: Run ----------
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=build /app/target/Social_Media-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]