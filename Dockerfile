# Stage 1: Build application using official Maven with Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Production runtime image
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Ensure persistent data directory exists for H2 database
RUN mkdir -p /app/data

# Copy the built jar from build stage
COPY --from=build /app/target/employee-management-system-1.0.0.jar app.jar

EXPOSE 8080
ENV PORT=8080

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]
