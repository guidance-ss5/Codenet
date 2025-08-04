# CODENET Backend Setup Guide

This guide will help you set up the complete CODENET backend with Java Spring Boot, Ballerina, Clerk authentication, and MongoDB.

## Prerequisites

1. **Java 17 or higher**
2. **Maven 3.6+**
3. **MongoDB** (local or MongoDB Atlas)
4. **Ballerina** (for webhook service)
5. **Clerk Account** (for authentication)

## Step 1: Clerk Setup

1. Go to [clerk.com](https://clerk.com) and create an account
2. Create a new application called "CODENET"
3. Get your API keys from the Clerk dashboard:
   - Publishable Key (for frontend)
   - Secret Key (for backend)
   - JWT Issuer URL (for JWT validation)

4. Configure webhooks in Clerk:
   - Go to Webhooks in your Clerk dashboard
   - Add endpoint: `https://your-domain.com/clerk-webhook`
   - Select events: `user.created`, `user.updated`, `user.deleted`
   - Copy the webhook secret

## Step 2: MongoDB Setup

### Option A: MongoDB Atlas (Recommended)
1. Go to [cloud.mongodb.com](https://cloud.mongodb.com)
2. Create a free cluster
3. Create a database user
4. Get your connection string
5. Update `application.properties` with your connection string

### Option B: Local MongoDB
1. Download and install MongoDB Community Server
2. Start MongoDB service
3. Create database: `codenet`

## Step 3: Update Configuration

Update `src/main/resources/application.properties`:

```properties
# MongoDB Configuration
spring.data.mongodb.uri=mongodb+srv://YOUR_USERNAME:YOUR_PASSWORD@YOUR_CLUSTER.mongodb.net/codenet?retryWrites=true&w=majority

# Clerk Configuration
clerk.publishable-key=pk_live_YOUR_PUBLISHABLE_KEY
clerk.secret-key=sk_live_YOUR_SECRET_KEY
clerk.jwt-issuer=https://clerk.YOUR_DOMAIN/.well-known/jwks.json

# File Upload Configuration
file.upload-dir=./uploads
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=50MB
```

## Step 4: Build and Run Spring Boot Backend

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

The Spring Boot application will start on `http://localhost:8080`

## Step 5: Setup Ballerina Webhook Service

1. Install Ballerina from [ballerina.io](https://ballerina.io/downloads/)

2. Update `clerk_webhook_listener/main.bal`:
   ```ballerina
   configurable string CLERK_WEBHOOK_SECRET = "whsec_YOUR_WEBHOOK_SECRET";
   configurable string SPRING_BOOT_USER_SYNC_URL = "http://localhost:8080/api/users/sync";
   ```

3. Run the Ballerina service:
   ```bash
   cd clerk_webhook_listener
   bal run
   ```

The Ballerina service will start on `http://localhost:9090`

## Step 6: Frontend Integration

1. Add Clerk SDK to your HTML files:
   ```html
   <script async defer src="https://YOUR_CLERK_DOMAIN/__clerk/js/clerk.browser.js" 
           data-clerk-publishable-key="pk_live_YOUR_PUBLISHABLE_KEY"></script>
   ```

2. Include the API client script:
   ```html
   <script src="api.js"></script>
   ```

3. Update your HTML forms to use `enctype="multipart/form-data"` for file uploads

## Step 7: Test the Setup

1. **Test Authentication**:
   - Visit your login page
   - Sign up/sign in with Clerk
   - Check if user is created in MongoDB

2. **Test Project Creation**:
   - Submit a project through the form
   - Check if project is saved in MongoDB
   - Verify file uploads work

3. **Test API Endpoints**:
   ```bash
   # Get all projects
   curl http://localhost:8080/api/projects
   
   # Get trending projects
   curl http://localhost:8080/api/projects/trending
   
   # Get stats
   curl http://localhost:8080/api/projects/stats
   ```

## API Endpoints

### Public Endpoints
- `GET /api/projects` - Get all approved projects
- `GET /api/projects/{id}` - Get project by ID
- `GET /api/projects/trending` - Get trending projects
- `GET /api/projects/featured` - Get featured projects
- `GET /api/projects/stats` - Get application stats
- `POST /api/users/sync` - Sync user from Clerk (webhook)

### Protected Endpoints (Require JWT)
- `POST /api/projects` - Create new project
- `PUT /api/projects/{id}` - Update project
- `DELETE /api/projects/{id}` - Delete project
- `PUT /api/projects/{id}/like` - Like a project
- `GET /api/projects/my` - Get user's projects
- `GET /api/users/me` - Get current user

### Admin Endpoints
- `GET /api/admin/projects/pending` - Get pending projects
- `PUT /api/admin/projects/{id}/approve` - Approve project
- `PUT /api/admin/projects/{id}/reject` - Reject project
- `PUT /api/admin/projects/{id}/feature` - Feature project

## Database Collections

### users
```json
{
  "_id": "clerk_user_id",
  "email": "user@example.com",
  "username": "username",
  "firstName": "John",
  "lastName": "Doe",
  "joinedAt": "2024-01-01T00:00:00Z",
  "role": "Developer",
  "profileImageUrl": "https://...",
  "isActive": true
}
```

### projects
```json
{
  "_id": "project_id",
  "title": "Project Title",
  "subtitle": "Project Subtitle",
  "description": "Project description",
  "ownerId": "clerk_user_id",
  "ownerName": "John Doe",
  "mediaUrls": ["/uploads/file1.jpg"],
  "likes": 10,
  "views": 100,
  "uploadDate": "2024-01-01T00:00:00Z",
  "status": "APPROVED",
  "tags": ["react", "javascript"],
  "githubUrl": "https://github.com/...",
  "liveDemoUrl": "https://demo.com",
  "category": "Web App",
  "technology": "React",
  "isFeatured": false
}
```

## Security Features

1. **JWT Authentication**: All protected endpoints require valid Clerk JWT tokens
2. **CORS Configuration**: Configured for frontend access
3. **File Upload Security**: File size limits and type validation
4. **Authorization**: Users can only modify their own projects
5. **Webhook Verification**: Ballerina service verifies webhook signatures

## Production Deployment

### Spring Boot
1. Build JAR: `mvn clean package`
2. Deploy to cloud platform (Heroku, AWS, etc.)
3. Set environment variables for production

### Ballerina
1. Build executable: `bal build`
2. Deploy as separate service
3. Configure production URLs

### MongoDB
- Use MongoDB Atlas for production
- Configure network access
- Set up backup and monitoring

### File Storage
- Replace local storage with AWS S3, Google Cloud Storage, or Azure Blob Storage
- Update `FileStorageService` to use cloud storage

## Troubleshooting

### Common Issues

1. **CORS Errors**: Check CORS configuration in `SecurityConfig`
2. **JWT Validation Fails**: Verify Clerk JWT issuer URL
3. **MongoDB Connection**: Check connection string and network access
4. **File Uploads**: Ensure upload directory exists and is writable
5. **Webhook Not Working**: Verify webhook URL and secret

### Logs
- Spring Boot logs: Check console output
- Ballerina logs: Check console output
- MongoDB logs: Check MongoDB logs

## Next Steps

1. **Add Admin Panel**: Create admin interface for project approval
2. **Implement Search**: Add advanced search functionality
3. **Add Categories**: Implement project categorization
4. **Add Comments**: Implement project commenting system
5. **Add Notifications**: Implement user notifications
6. **Add Analytics**: Track user behavior and project performance
7. **Add Payment Integration**: For premium features
8. **Add Social Features**: Follow users, share projects

## Support

For issues and questions:
1. Check the logs for error messages
2. Verify all configuration values
3. Test endpoints individually
4. Check MongoDB connection
5. Verify Clerk webhook configuration 