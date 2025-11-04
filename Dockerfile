# ✅ Use official Java 17 runtime
FROM openjdk:17-jdk-slim

# ✅ Set working directory inside container
WORKDIR /app

# ✅ Copy all project files into the container
COPY . .

# ✅ Build the Spring Boot app (skip tests for faster build)
RUN ./mvnw clean package -DskipTests || mvn clean package -DskipTests

# ✅ Run the app when container starts
CMD ["java", "-jar", "target/StartXAIChatBot-1.0.0.jar"]
