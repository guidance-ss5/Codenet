@echo off
REM Codenet Deployment Script for Windows
REM Usage: deploy.bat [environment]
REM Environments: dev, staging, prod

setlocal enabledelayedexpansion

set ENVIRONMENT=%1
if "%ENVIRONMENT%"=="" set ENVIRONMENT=dev

echo 🚀 Starting deployment for environment: %ENVIRONMENT%

REM Check prerequisites
echo [INFO] Checking prerequisites...

REM Check if Docker is installed
docker --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker is not installed. Please install Docker first.
    exit /b 1
)

REM Check if Docker Compose is installed
docker-compose --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker Compose is not installed. Please install Docker Compose first.
    exit /b 1
)

REM Check if .env file exists
if not exist ".env" (
    echo [WARNING] .env file not found. Copying from .env.example
    if exist ".env.example" (
        copy ".env.example" ".env"
        echo [WARNING] Please update .env file with your actual values before continuing.
        exit /b 1
    ) else (
        echo [ERROR] .env.example not found. Please create environment configuration.
        exit /b 1
    )
)

echo [INFO] Prerequisites check completed ✅

REM Build backend
echo [INFO] Building Spring Boot backend...
cd backend

REM Clean and build
call mvnw.cmd clean package -DskipTests
if errorlevel 1 (
    echo [ERROR] Backend build failed ❌
    exit /b 1
)

echo [INFO] Backend build completed ✅
cd ..

REM Build Ballerina webhook service
echo [INFO] Building Ballerina webhook service...
cd clerk_webhook_listener

REM Build Ballerina project
bal build
if errorlevel 1 (
    echo [ERROR] Webhook service build failed ❌
    exit /b 1
)

echo [INFO] Webhook service build completed ✅
cd ..

REM Deploy with Docker Compose
echo [INFO] Deploying with Docker Compose...

REM Set environment variables
if "%ENVIRONMENT%"=="dev" set SPRING_PROFILES_ACTIVE=dev
if "%ENVIRONMENT%"=="staging" set SPRING_PROFILES_ACTIVE=staging
if "%ENVIRONMENT%"=="prod" set SPRING_PROFILES_ACTIVE=prod
if "%ENVIRONMENT%"=="production" set SPRING_PROFILES_ACTIVE=prod

REM Stop existing containers
docker-compose down

REM Build and start containers
docker-compose up --build -d
if errorlevel 1 (
    echo [ERROR] Deployment failed ❌
    exit /b 1
)

echo [INFO] Deployment completed ✅
echo [INFO] Services are starting up...

REM Wait for services
timeout /t 30 /nobreak >nul

REM Show deployment info
echo.
echo 🎉 Deployment Information:
echo   Environment: %ENVIRONMENT%
echo   Backend API: http://localhost:8080
echo   Frontend: http://localhost (if using nginx)
echo   MongoDB: localhost:27017
echo   Webhook Service: http://localhost:9090
echo.
echo 📋 Useful Commands:
echo   View logs: docker-compose logs -f [service]
echo   Stop services: docker-compose down
echo   Restart service: docker-compose restart [service]
echo   Check status: docker-compose ps
echo.
echo 🎯 Deployment completed successfully!

endlocal
