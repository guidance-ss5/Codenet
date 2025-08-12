# 🌐 Codenet - Full-Stack Project Platform

A comprehensive platform for developers to showcase, share, and discover coding projects. Built with Spring Boot, MongoDB, Clerk Authentication, and Ballerina webhook services.

## 🏗️ Architecture

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Spring Boot    │    │   MongoDB       │
│   (HTML/CSS/JS) │◄──►│   Backend API    │◄──►│   Database      │
└─────────────────┘    └──────────────────┘    └─────────────────┘
         │                       │                       
         │               ┌──────────────────┐            
         └──────────────►│   Clerk Auth     │            
                         │   Service        │            
                         └──────────────────┘            
                                 │                       
                         ┌──────────────────┐            
                         │   Ballerina      │            
                         │   Webhook        │            
                         └──────────────────┘            
```

## 🚀 Quick Start

### Prerequisites
- **Java 21+** (for Spring Boot backend)
- **Node.js 18+** (for frontend development)
- **Ballerina 2201.8.0+** (for webhook service)
- **Docker & Docker Compose** (for containerized deployment)
- **MongoDB** (local or MongoDB Atlas)
- **Clerk Account** (for authentication)

### 1. Clone Repository
```bash
git clone https://github.com/guidance-ss5/Codenet.git
cd Codenet
```

### 2. Environment Setup
```bash
# Copy environment template
cp .env.example .env

# Edit .env with your actual values
nano .env
```

### 3. Quick Deploy (Recommended)
```bash
# For Linux/Mac
chmod +x deploy.sh
./deploy.sh dev

# For Windows
deploy.bat dev
```

### 4. Manual Setup (Alternative)

#### Backend Setup
```bash
cd backend
./mvnw clean install
./mvnw spring-boot:run
```

#### Webhook Service Setup
```bash
cd clerk_webhook_listener
bal build
bal run
```

#### Frontend Setup
```bash
cd frontend
# Open index.html in browser or serve with live server
python -m http.server 8000  # Python way
# OR
npx live-server  # Node.js way
```

## 🔧 Configuration

### Environment Variables

| Variable | Description | Required | Default |
|----------|-------------|----------|---------|
| `MONGODB_URI` | MongoDB connection string | ✅ | `mongodb://localhost:27017/codenet` |
| `CLERK_PUBLISHABLE_KEY` | Clerk publishable key | ✅ | - |
| `CLERK_SECRET_KEY` | Clerk secret key | ✅ | - |
| `CLERK_JWT_ISSUER` | Clerk JWT issuer URL | ✅ | - |
| `ALLOWED_ORIGINS` | CORS allowed origins | ❌ | `http://localhost:*` |
| `MAX_FILE_SIZE` | Maximum file upload size | ❌ | `10MB` |
| `UPLOAD_DIR` | File upload directory | ❌ | `./uploads` |

### Application Profiles

- **`dev`**: Development with debug logging
- **`staging`**: Staging environment with reduced logging
- **`prod`**: Production with security headers and minimal logging

## 📋 API Documentation

### Authentication
All protected endpoints require Bearer token in Authorization header:
```
Authorization: Bearer <clerk_jwt_token>
```

### Main Endpoints

#### Projects
- `GET /api/projects/trending` - Get trending projects
- `GET /api/projects/search?q={query}` - Search projects
- `GET /api/projects/stats` - Get platform statistics
- `POST /api/projects` - Create new project (Auth required)
- `GET /api/projects/my` - Get user's projects (Auth required)
- `POST /api/projects/{id}/like` - Like a project (Auth required)

#### Users
- `POST /api/users/sync` - Sync user from Clerk webhook
- `GET /api/users/profile` - Get user profile (Auth required)

#### Admin
- `GET /api/admin/projects/pending` - Get pending projects (Admin only)
- `POST /api/admin/projects/{id}/approve` - Approve project (Admin only)

## 🐳 Docker Deployment

### Development
```bash
docker-compose up --build
```

### Production
```bash
# Set production environment
export SPRING_PROFILES_ACTIVE=prod

# Deploy with production overrides
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

### Services

| Service | Port | Description |
|---------|------|-------------|
| Backend | 8080 | Spring Boot API |
| MongoDB | 27017 | Database |
| Webhook | 9090 | Ballerina webhook service |
| Nginx | 80/443 | Frontend server (production) |

## 🔒 Security Features

### Backend Security
- **JWT Authentication** with Clerk integration
- **CORS Configuration** with environment-specific origins
- **Rate Limiting** (100 requests/minute default)
- **Input Validation** with Jakarta Bean Validation
- **Security Headers** (HSTS, X-Frame-Options, etc.)
- **File Upload Validation** (size, type restrictions)

### Frontend Security
- **Environment-based Configuration**
- **XSS Protection** through content sanitization
- **Rate Limiting** awareness and retry logic
- **Secure Token Handling**

## 📊 Performance & Monitoring

### Health Checks
- **Backend**: `http://localhost:8080/actuator/health`
- **MongoDB**: Connection validation
- **File System**: Upload directory access

### Monitoring
- **Application Metrics** via Spring Actuator
- **Error Tracking** with structured logging
- **Performance Monitoring** (configurable)

## 🧪 Testing

### Backend Tests
```bash
cd backend
./mvnw test
```

### Integration Tests
```bash
./mvnw verify
```

### Load Testing
```bash
# Using Apache Bench
ab -n 1000 -c 10 http://localhost:8080/api/projects/trending

# Using curl for specific endpoints
curl -X GET http://localhost:8080/actuator/health
```

## 🚀 Deployment Strategies

### 1. Local Development
```bash
./deploy.sh dev
```

### 2. Docker Compose (Recommended)
```bash
docker-compose up -d
```

### 3. Kubernetes
```bash
kubectl apply -f k8s/
```

### 4. Cloud Deployment

#### AWS Elastic Beanstalk
1. Build JAR: `./mvnw clean package`
2. Upload to Elastic Beanstalk
3. Configure environment variables

#### Heroku
```bash
heroku create codenet-app
heroku config:set SPRING_PROFILES_ACTIVE=prod
git push heroku main
```

#### Railway
```bash
railway login
railway init
railway up
```

## 🔧 Troubleshooting

### Common Issues

#### 1. MongoDB Connection Failed
```bash
# Check MongoDB status
docker-compose logs mongodb

# Restart MongoDB
docker-compose restart mongodb
```

#### 2. CORS Errors
```bash
# Update CORS configuration in application.properties
spring.web.cors.allowed-origins=http://localhost:3000,https://yourdomain.com
```

#### 3. File Upload Errors
```bash
# Check upload directory permissions
ls -la uploads/
chmod 755 uploads/

# Check file size limits
# Update in application.properties:
spring.servlet.multipart.max-file-size=10MB
```

#### 4. Rate Limiting Issues
```bash
# Increase rate limits in RateLimitingFilter.java
# Or add IP to whitelist
```

### Logs Access
```bash
# Backend logs
docker-compose logs -f backend

# All services
docker-compose logs -f

# Specific timeframe
docker-compose logs --since="2h" backend
```

## 🔄 CI/CD Pipeline

### GitHub Actions
- **Automated Testing** on push/PR
- **Security Scanning** with Trivy
- **Docker Build** and push to registry
- **Deployment** to staging/production

### Pipeline Stages
1. **Test** - Backend and frontend tests
2. **Build** - Maven build and Docker images
3. **Security** - Vulnerability scanning
4. **Deploy** - Automated deployment

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

### Development Guidelines
- Follow Java Spring Boot best practices
- Use meaningful commit messages
- Add tests for new features
- Update documentation

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Spring Boot** - Backend framework
- **MongoDB** - Database
- **Clerk** - Authentication service
- **Ballerina** - Webhook service
- **Docker** - Containerization

## 📞 Support

- **Documentation**: Check this README and inline code comments
- **Issues**: Create GitHub issues for bugs/features
- **Discussions**: Use GitHub Discussions for questions

---

**Happy Coding! 🚀**
