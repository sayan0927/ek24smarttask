# Use an official OpenJDK 20 runtime as a parent image
FROM openjdk:20-jdk-slim

# Set the working directory in the container
WORKDIR /core


# Copy the project jar file to the workdir
COPY target/smart_task_management-0.0.1-SNAPSHOT.jar app.jar

# Make port 8000 available to the world outside this container
EXPOSE 8000

# Set environment variables
ENV JAVA_OPTS=""



# Run the jar file
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]





