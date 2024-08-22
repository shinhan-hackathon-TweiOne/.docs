# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk

# Set the JAR directory in the container
ARG JAR_FILE=build/libs/*.jar

# Copy the packaged jar file into the container
COPY ${JAR_FILE} app.jar

# Run the jar file
ENTRYPOINT ["java","-jar","app.jar"]