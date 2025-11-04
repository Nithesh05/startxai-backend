# ✅ Use reliable Java 17 runtime (Render compatible)
FROM eclipse-temurin:17-jdk-jammy

# ✅ Set working directory
WORKDIR /app

# ✅ Copy all files into container
COPY . .

# ✅ Build Spring Boot app (skip tests for faster build)
RUN ./mvnw clean package -DskipTests || mvn clean package -DskipTests

# ✅ Run the app when container starts
CMD ["java", "-jar", "target/StartXAIChatBot-1.0.0.jar"]
