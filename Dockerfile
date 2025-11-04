# ✅ Use a full JDK + Maven image for reliable build on Render
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy all files into the container
COPY . .

# ✅ Build the JAR file (skip tests to make build faster)
RUN mvn clean package -DskipTests

# ✅ Use lightweight Java runtime for final image
FROM eclipse-temurin:17-jdk-jammy

# Set working directory again
WORKDIR /app

# Copy built jar from the previous stage
COPY --from=build /app/target/StartXAIChatBot-1.0.0.jar /app/StartXAIChatBot-1.0.0.jar

# ✅ Run the JAR
CMD ["java", "-jar", "StartXAIChatBot-1.0.0.jar"]
