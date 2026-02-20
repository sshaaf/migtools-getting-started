#!/bin/bash

#####################################################
# PetClinic Deployment Script
# Deploys the application using java -jar with
# parameters and environment variables
#####################################################

set -e

# Script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JAR_FILE="$SCRIPT_DIR/target/petclinic-hello-1.0.0.jar"

# Default values
DEFAULT_PORT=8080
DEFAULT_ENVIRONMENT="development"
DEFAULT_MESSAGE="Welcome to PetClinic!"
DEFAULT_HEAP_SIZE="512m"

# Environment variables (can be overridden)
export APP_PORT="${APP_PORT:-$DEFAULT_PORT}"
export APP_ENVIRONMENT="${APP_ENVIRONMENT:-$DEFAULT_ENVIRONMENT}"
export APP_MESSAGE="${APP_MESSAGE:-$DEFAULT_MESSAGE}"
export JAVA_HEAP_SIZE="${JAVA_HEAP_SIZE:-$DEFAULT_HEAP_SIZE}"

# Color output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  PetClinic Deployment Script${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# Check if JAR exists
if [ ! -f "$JAR_FILE" ]; then
    echo -e "${YELLOW}JAR file not found. Building application...${NC}"
    cd "$SCRIPT_DIR"
    ./mvnw clean package -DskipTests
    echo ""
fi

# Display configuration
echo -e "${GREEN}Configuration:${NC}"
echo "  Port:        $APP_PORT"
echo "  Environment: $APP_ENVIRONMENT"
echo "  Message:     $APP_MESSAGE"
echo "  Heap Size:   $JAVA_HEAP_SIZE"
echo ""

# Build JVM arguments
JVM_ARGS=(
    "-Xmx${JAVA_HEAP_SIZE}"
    "-Xms${JAVA_HEAP_SIZE}"
    "-Djava.security.egd=file:/dev/./urandom"
)

# Build application arguments
APP_ARGS=(
    "--server.port=${APP_PORT}"
    "--app.environment=${APP_ENVIRONMENT}"
    "--app.message=${APP_MESSAGE}"
)

echo -e "${GREEN}Starting PetClinic application...${NC}"
echo ""

# Run the application
java "${JVM_ARGS[@]}" -jar "$JAR_FILE" "${APP_ARGS[@]}"
