FROM maven:3.9.8-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM arm64v8/openjdk:21-ea-21-jdk-slim
COPY --from=build /target/Social_Media-0.0.1-SNAPSHOT.jar  Social_Media.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","Social_Media.jar"]