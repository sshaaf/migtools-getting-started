#!/bin/bash

#####################################################
# PetClinic Stop Script
# Stops the running PetClinic application
#####################################################

APP_PORT="${APP_PORT:-8080}"

echo "Stopping PetClinic application on port $APP_PORT..."

# Find process using the port
PID=$(lsof -ti:$APP_PORT)

if [ -z "$PID" ]; then
    echo "No application found running on port $APP_PORT"
    exit 0
fi

echo "Found process $PID, stopping..."
kill $PID

# Wait for process to stop
sleep 2

# Check if still running
if lsof -ti:$APP_PORT > /dev/null 2>&1; then
    echo "Process still running, forcing shutdown..."
    kill -9 $PID
fi

echo "Application stopped successfully"
