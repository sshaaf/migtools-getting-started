# PetClinic Hello World - Shell Script Deployment

A simple Spring Boot hello world application styled after PetClinic, designed to be deployed using shell scripts with `java -jar` and configurable via parameters and environment variables.

## Project Structure

```
shell-script-rules/
├── src/main/java/com/example/petclinic/
│   ├── PetClinicApplication.java    # Main Spring Boot application
│   └── HelloController.java         # REST controller with endpoints
├── src/main/resources/
│   └── application.properties       # Default configuration
├── pom.xml                          # Maven build configuration
├── deploy.sh                        # Deployment script
├── stop.sh                          # Stop script
└── README.md                        # This file
```

## Prerequisites

- Java 17 or higher
- Maven 3.6+ (or use included Maven wrapper)

## Building the Application

```bash
./mvnw clean package
```

This creates an executable JAR at `target/petclinic-hello-1.0.0.jar`

## Deployment

### Basic Deployment

Run with default settings (port 8080, development environment):

```bash
chmod +x deploy.sh
./deploy.sh
```

### Deployment with Environment Variables

Configure the application using environment variables:

```bash
# Set custom port
export APP_PORT=9090

# Set environment name
export APP_ENVIRONMENT=production

# Set custom welcome message
export APP_MESSAGE="Welcome to our Pet Care Center!"

# Set JVM heap size
export JAVA_HEAP_SIZE=1024m

# Deploy
./deploy.sh
```

### One-line Deployment

```bash
APP_PORT=9090 APP_ENVIRONMENT=staging APP_MESSAGE="Staging PetClinic" ./deploy.sh
```

## Stopping the Application

```bash
chmod +x stop.sh
./stop.sh
```

Or with custom port:

```bash
APP_PORT=9090 ./stop.sh
```

## Available Endpoints

Once running, the following endpoints are available:

- `http://localhost:8080/` - Main hello endpoint
  - Returns: application message, environment, timestamp, port, status

- `http://localhost:8080/health` - Health check endpoint
  - Returns: status and environment

- `http://localhost:8080/pets` - PetClinic services info
  - Returns: available services information

- `http://localhost:8080/actuator/health` - Spring Actuator health endpoint

## Configuration Options

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `APP_PORT` | Server port | 8080 |
| `APP_ENVIRONMENT` | Environment name | development |
| `APP_MESSAGE` | Welcome message | Welcome to PetClinic! |
| `JAVA_HEAP_SIZE` | JVM heap size | 512m |

### JVM Arguments

The deploy script automatically sets:
- `-Xmx${JAVA_HEAP_SIZE}` - Maximum heap size
- `-Xms${JAVA_HEAP_SIZE}` - Initial heap size
- `-Djava.security.egd=file:/dev/./urandom` - Faster startup

## Testing the Application

```bash
# Start the application
./deploy.sh

# In another terminal, test the endpoints
curl http://localhost:8080/
curl http://localhost:8080/health
curl http://localhost:8080/pets
```

## Example Output

```json
{
  "message": "Welcome to PetClinic!",
  "environment": "development",
  "timestamp": "2024-01-20T10:30:45.123",
  "port": "8080",
  "status": "running"
}
```

## Use Case

This project demonstrates a typical deployment pattern where applications are deployed using shell scripts with `java -jar`, commonly found in:
- Legacy deployment environments
- Migration scenarios from traditional deployment to containerized environments
- Systems that haven't yet adopted container orchestration
- Simple deployment scenarios without CI/CD pipelines

This pattern is useful for migration analysis tools like Konveyor to identify and create rules for modernizing deployment strategies.
