# Use Maven and JDK base image
FROM maven:3.8-openjdk-17 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code and build the application
COPY src /app/src
RUN mvn clean package -DskipTests

# Use a smaller image for the final stage
FROM openjdk:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/redditclone-0.0.1-SNAPSHOT.jar /app/redditclone.jar

# Run the application
ENTRYPOINT ["java", "-jar", "/app/redditclone.jar"]
