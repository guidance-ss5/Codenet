# JWT Token Usage Examples

## Overview
The `api.js` file provides a complete API client with automatic JWT token handling for all backend requests.

## Basic Usage

### 1. Get Authentication Token
```javascript
// Get JWT token for authenticated requests
async function getAuthToken() {
    if (window.Clerk && window.Clerk.session) {
        return await window.Clerk.session.getToken();
    }
    return null;
}

// Example usage
const token = await api.getAuthToken();
console.log('JWT Token:', token);
```

### 2. Make Authenticated API Calls
```javascript
// The API client automatically includes the JWT token in headers
try {
    // Get current user data from backend
    const user = await api.getCurrentUser();
    console.log('User data from backend:', user);
    
    // Get user's projects
    const projects = await api.getMyProjects();
    console.log('User projects:', projects);
    
    // Create a new project
    const newProject = await api.createProject({
        title: 'My Project',
        description: 'Project description',
        subtitle: 'Subtitle'
    });
    
} catch (error) {
    console.error('API call failed:', error);
}
```

### 3. Manual Requests with JWT
```javascript
// If you need to make custom requests manually
async function customAPICall() {
    const token = await api.getAuthToken();
    
    const response = await fetch('http://localhost:8080/api/projects', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    });
    
    if (response.ok) {
        const data = await response.json();
        return data;
    } else {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
    }
}
```

## Available API Methods

### Project Management
```javascript
// Get all projects (public)
const projects = await api.getProjects();

// Search projects
const searchResults = await api.getProjects('search term');

// Get project by ID
const project = await api.getProjectById('project-id');

// Get current user's projects
const myProjects = await api.getMyProjects();

// Create project with files
const project = await api.createProject(projectData, mediaFiles);

// Update project
const updated = await api.updateProject('project-id', updatedData);

// Delete project
await api.deleteProject('project-id');

// Like a project
await api.likeProject('project-id');

// Get trending projects
const trending = await api.getTrendingProjects();
```

### User Management
```javascript
// Get current user from backend
const currentUser = await api.getCurrentUser();

// Get user by ID
const user = await api.getUserById('user-id');
```

### Statistics
```javascript
// Get platform statistics
const stats = await api.getStats();
```

## Authentication Check
```javascript
// Check if user is authenticated
if (api.isAuthenticated()) {
    console.log('User is logged in');
} else {
    console.log('User needs to log in');
    api.requireAuth(); // Redirects to login page
}
```

## Error Handling
```javascript
try {
    const data = await api.getMyProjects();
    // Handle success
} catch (error) {
    if (error.message.includes('401')) {
        // Token expired or invalid - redirect to login
        window.location.href = 'login.html';
    } else {
        // Other errors
        console.error('API Error:', error.message);
        alert('Something went wrong. Please try again.');
    }
}
```

## Implementation Status

### ✅ Completed
- JWT token retrieval from Clerk session
- Automatic token inclusion in API requests  
- Complete API client with all backend endpoints
- Error handling and authentication checks
- Project CRUD operations
- User management methods
- File upload support for projects

### ✅ Examples Added
- `userprofile.html` now includes:
  - Backend connection test
  - JWT token demonstration
  - User projects loading from backend
  - Error handling examples

### 🔧 Ready for Use
The JWT token handling is now fully implemented and demonstrated. Users can:
1. See JWT tokens in browser console
2. Test backend connectivity
3. Load their projects from the backend
4. See real-time authentication status

## Testing the Integration

1. **Start your Spring Boot backend** on `localhost:8080`
2. **Configure Clerk credentials** in HTML files
3. **Open userprofile.html** in browser
4. **Log in with Clerk**
5. **Check browser console** for JWT token and API calls
6. **Click "Load My Projects"** to test backend integration

The system will show whether the backend is reachable and JWT authentication is working properly.
