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

## ✅ **Recent Improvements & Fixes**

### **Resolved Issues:**
1. **Merge Conflicts Fixed:**
   - ✅ ProjectController.java - All conflicts resolved
   - ✅ submitproject.html - Form structure cleaned up
   - ✅ codenet.css - CSS variables unified
   - ✅ application.properties - Configuration merged

2. **Empty Files Completed:**
   - ✅ UserRepository.java - MongoDB repository interface
   - ✅ User.java - User model with Lombok
   - ✅ LoginRequest.java - Authentication DTO
   - ✅ AuthController.java - Basic auth endpoints
   - ✅ AuthService.java - Authentication service
   - ✅ Dockerfile - Spring Boot containerization
   - ✅ main.bal - Ballerina webhook service

3. **Security Improvements:**
   - ✅ JWT validation service implemented
   - ✅ CORS configuration updated
   - ✅ Security headers configured
   - ✅ Rate limiting implemented
   - ✅ Webhook signature verification (simplified)

### **Current Status:**
- **Frontend:** ✅ Ready for development
- **Backend:** ⚠️ Some compilation issues with Lombok
- **Webhook Service:** ⚠️ Ballerina syntax needs review
- **Database:** ✅ MongoDB configuration ready
- **Authentication:** ✅ Clerk integration configured

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
- **`prod`**: Production with optimized settings

## 🚨 **Remaining Issues to Address**

### **Critical (Must Fix):**
1. **Backend Compilation:**
   - Lombok annotation processing issues
   - Missing getter/setter methods in Project model
   - ProjectService method signature mismatches

2. **Ballerina Webhook:**
   - Syntax errors in main.bal
   - Need to verify Ballerina version compatibility
   - Webhook signature verification implementation

### **Important (Should Fix):**
1. **Security Hardening:**
   - Move sensitive keys to environment variables
   - Implement proper webhook signature verification
   - Add input validation and sanitization

2. **Error Handling:**
   - Consistent error responses across all endpoints
   - Proper logging configuration
   - User-friendly error messages

3. **Testing:**
   - Unit tests for critical components
   - Integration tests for API endpoints
   - End-to-end testing

### **Enhancement (Nice to Have):**
1. **Performance:**
   - Implement caching for frequently accessed data
   - Optimize database queries
   - Add pagination for large datasets

2. **User Experience:**
   - Better loading states
   - Improved error messages
   - Mobile responsiveness improvements

## 📁 Project Structure

```
Codenet/
├── frontend/                 # Static HTML/CSS/JS
│   ├── *.html              # Main pages
│   ├── codenet.css         # Styles
│   ├── api.js              # API client
│   └── config.js           # Environment config
├── backend/                 # Spring Boot API
│   ├── src/main/java/
│   │   └── com/codenet/codenetbackend/
│   │       ├── controller/ # REST endpoints
│   │       ├── service/    # Business logic
│   │       ├── repository/ # Data access
│   │       ├── model/      # Data models
│   │       └── config/     # Configuration
│   └── pom.xml
├── clerk_webhook_listener/  # Ballerina webhook service
│   └── main.bal
├── docker-compose.yml       # Container orchestration
└── README.md
```

## 🔒 Security Features

- **JWT Authentication** with Clerk integration
- **CORS Configuration** for cross-origin requests
- **Rate Limiting** to prevent abuse
- **Input Validation** with Bean Validation
- **Security Headers** (HSTS, X-Frame-Options, etc.)
- **Webhook Signature Verification** (simplified)

## 🚀 Deployment

### Docker Compose (Recommended)
```bash
docker-compose up -d
```

### Manual Deployment
1. Build backend: `./mvnw clean package`
2. Start MongoDB
3. Run backend: `java -jar target/codenetbackend-1.0.0.jar`
4. Serve frontend with any static file server

## 📝 API Documentation

Once the backend is running, visit:
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **API Docs**: `http://localhost:8080/v3/api-docs`

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:
- **Email**: binupanuransith@gmail.com
- **Phone**: +94-77-483-7197

---

**Note**: This project is currently in development. Some features may not be fully functional until the remaining issues are resolved.
