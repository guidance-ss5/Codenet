# Codenet Backend

A Spring Boot backend for the Codenet project sharing platform.

## Features

- **User Management**: Integration with Clerk authentication
- **Project Management**: CRUD operations for projects
- **File Upload**: Media upload support for projects
- **Admin Panel**: Project moderation and approval workflow
- **Search & Discovery**: Project search and trending projects
- **Like System**: User engagement through project likes

## Setup

### Prerequisites

- Java 17
- Maven
- MongoDB (running on localhost:27017)

### Running the Application

1. Start MongoDB:
   ```bash
   mongod
   ```

2. Build and run the application:
   ```bash
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080`

## API Endpoints

### User Management
- `POST /api/users/sync` - Sync user data from Clerk
- `DELETE /api/users/sync/{clerkUserId}` - Delete user
- `GET /api/users/me` - Get current user details

### Project Management
- `POST /api/projects` - Create a new project
- `GET /api/projects` - Get all approved projects
- `GET /api/projects/{id}` - Get project by ID
- `PUT /api/projects/{id}` - Update project
- `DELETE /api/projects/{id}` - Delete project
- `PUT /api/projects/{id}/like` - Like a project
- `GET /api/projects/trending` - Get trending projects
- `GET /api/projects/stats` - Get project statistics

### File Upload
- `POST /api/upload/media` - Upload media files

### Admin Panel
- `GET /api/admin/projects/pending` - Get pending projects
- `PUT /api/admin/projects/{id}/approve` - Approve project
- `PUT /api/admin/projects/{id}/reject` - Reject project
- `GET /api/admin/stats` - Get admin statistics

## Project Status

Projects can have the following statuses:
- `PENDING` - Awaiting admin approval
- `APPROVED` - Approved and visible to users
- `REJECTED` - Rejected by admin

## Authentication

The backend integrates with Clerk for authentication. JWT tokens from Clerk should be included in the `Authorization` header as `Bearer <token>`.

## File Upload

Media files are stored in the `uploads/` directory and served via `/uploads/{filename}`. 