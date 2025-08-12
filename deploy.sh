#!/bin/bash

# Codenet Deployment Script
# Usage: ./deploy.sh [environment]
# Environments: dev, staging, prod

set -e  # Exit on any error

ENVIRONMENT=${1:-dev}
PROJECT_NAME="codenet"

echo "🚀 Starting deployment for environment: $ENVIRONMENT"

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check prerequisites
check_prerequisites() {
    print_status "Checking prerequisites..."
    
    # Check if Docker is installed
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed. Please install Docker first."
        exit 1
    fi
    
    # Check if Docker Compose is installed
    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose is not installed. Please install Docker Compose first."
        exit 1
    fi
    
    # Check if .env file exists
    if [ ! -f ".env" ]; then
        print_warning ".env file not found. Copying from .env.example"
        if [ -f ".env.example" ]; then
            cp .env.example .env
            print_warning "Please update .env file with your actual values before continuing."
            exit 1
        else
            print_error ".env.example not found. Please create environment configuration."
            exit 1
        fi
    fi
    
    print_status "Prerequisites check completed ✅"
}

# Build backend
build_backend() {
    print_status "Building Spring Boot backend..."
    
    cd backend
    
    # Make mvnw executable
    chmod +x mvnw
    
    # Clean and build
    ./mvnw clean package -DskipTests
    
    if [ $? -eq 0 ]; then
        print_status "Backend build completed ✅"
    else
        print_error "Backend build failed ❌"
        exit 1
    fi
    
    cd ..
}

# Build Ballerina webhook service
build_webhook() {
    print_status "Building Ballerina webhook service..."
    
    cd clerk_webhook_listener
    
    # Build Ballerina project
    bal build
    
    if [ $? -eq 0 ]; then
        print_status "Webhook service build completed ✅"
    else
        print_error "Webhook service build failed ❌"
        exit 1
    fi
    
    cd ..
}

# Deploy with Docker Compose
deploy_docker() {
    print_status "Deploying with Docker Compose..."
    
    # Set environment-specific compose file
    COMPOSE_FILE="docker-compose.yml"
    if [ -f "docker-compose.$ENVIRONMENT.yml" ]; then
        COMPOSE_FILE="docker-compose.yml:docker-compose.$ENVIRONMENT.yml"
    fi
    
    # Stop existing containers
    docker-compose -f $COMPOSE_FILE down
    
    # Build and start containers
    docker-compose -f $COMPOSE_FILE up --build -d
    
    if [ $? -eq 0 ]; then
        print_status "Deployment completed ✅"
        print_status "Services are starting up..."
        
        # Wait for services to be healthy
        sleep 30
        
        # Check service health
        check_service_health
    else
        print_error "Deployment failed ❌"
        exit 1
    fi
}

# Check service health
check_service_health() {
    print_status "Checking service health..."
    
    # Check backend health
    for i in {1..12}; do
        if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
            print_status "Backend service is healthy ✅"
            break
        else
            if [ $i -eq 12 ]; then
                print_error "Backend service health check failed ❌"
                docker-compose logs backend
            else
                print_status "Waiting for backend service... ($i/12)"
                sleep 10
            fi
        fi
    done
    
    # Check MongoDB
    if docker-compose exec -T mongodb mongosh --eval "db.adminCommand('ping')" > /dev/null 2>&1; then
        print_status "MongoDB is healthy ✅"
    else
        print_warning "MongoDB health check failed ⚠️"
    fi
}

# Show deployment info
show_deployment_info() {
    print_status "🎉 Deployment Information:"
    echo "  Environment: $ENVIRONMENT"
    echo "  Backend API: http://localhost:8080"
    echo "  Frontend: http://localhost (if using nginx)"
    echo "  MongoDB: localhost:27017"
    echo "  Webhook Service: http://localhost:9090"
    echo ""
    print_status "📋 Useful Commands:"
    echo "  View logs: docker-compose logs -f [service]"
    echo "  Stop services: docker-compose down"
    echo "  Restart service: docker-compose restart [service]"
    echo "  Check status: docker-compose ps"
}

# Cleanup function
cleanup() {
    print_status "Cleaning up..."
    docker-compose down
    docker system prune -f
}

# Main deployment process
main() {
    case $ENVIRONMENT in
        dev|development)
            export SPRING_PROFILES_ACTIVE=dev
            ;;
        staging)
            export SPRING_PROFILES_ACTIVE=staging
            ;;
        prod|production)
            export SPRING_PROFILES_ACTIVE=prod
            ;;
        *)
            print_error "Invalid environment: $ENVIRONMENT"
            print_error "Valid environments: dev, staging, prod"
            exit 1
            ;;
    esac
    
    print_status "🏗️  Starting Codenet deployment for $ENVIRONMENT environment"
    
    check_prerequisites
    build_backend
    build_webhook
    deploy_docker
    show_deployment_info
    
    print_status "🎯 Deployment completed successfully!"
}

# Handle script interruption
trap cleanup EXIT

# Run main function
main
