# Clerk Authentication Setup Guide

## Overview
The frontend has been updated to use Clerk for authentication. You need to configure your Clerk credentials in the HTML files.

## Setup Steps

### 1. Get Your Clerk Credentials
1. Go to [Clerk.dev](https://clerk.dev) and create an account
2. Create a new application
3. Get your **Publishable Key** from the dashboard
4. Note your **Frontend API domain** 

### 2. Update HTML Files
Replace the placeholder values in these files:

#### Files to update:
- `login.html`
- `signup.html` 
- `userprofile.html`
- `codenet.html`

#### Replace these placeholders:
```html
<!-- Replace this line in each file: -->
<script async defer src="https://YOUR_CLERK_PUBLISHABLE_KEY_DOMAIN/__clerk/js/clerk.browser.js" data-clerk-publishable-key="pk_live_YOUR_CLERK_PUBLISHABLE_KEY"></script>

<!-- With your actual values: -->
<script async defer src="https://your-app-domain.clerk.accounts.dev/__clerk/js/clerk.browser.js" data-clerk-publishable-key="pk_live_your_actual_publishable_key"></script>
```

### 3. Backend Configuration
Make sure your Spring Boot backend has the matching Clerk configuration:

1. Update `application.properties`:
```properties
clerk.secret-key=sk_live_your_secret_key
clerk.issuer-url=https://your-app-domain.clerk.accounts.dev/.well-known/jwks.json
```

2. Update `JwtValidationService` if needed with your Clerk domain.

### 4. Test the Integration
1. Start your Spring Boot backend
2. Open `codenet.html` in a browser
3. Try logging in/signing up
4. Check that user data appears in your MongoDB database

## Features Implemented

### ✅ Login Page (`login.html`)
- Clerk sign-in component
- Redirects to `codenet.html` after successful login
- Link to signup page

### ✅ Signup Page (`signup.html`) 
- Clerk sign-up component
- Redirects to `codenet.html` after successful signup
- Link to login page

### ✅ User Profile (`userprofile.html`)
- Displays user information from Clerk
- Logout functionality
- Authentication check (redirects to login if not authenticated)
- Option to use Clerk's managed profile UI (commented out)

### ✅ Main Page (`codenet.html`)
- Dynamic authentication status in header
- Shows "Welcome, [Name]" when logged in
- Profile and logout links when authenticated
- Login/signup link when not authenticated

## Customization Options

### Use Clerk's Managed Profile UI
In `userprofile.html`, uncomment these lines to use Clerk's built-in profile component:
```javascript
// Clerk.mountUserProfile(document.getElementById('user-profile-container'));
// document.querySelector('.profile-header-card').style.display = 'none';
// document.getElementById('user-profile-container').style.display = 'block';
```

### Styling
The CSS includes styles for:
- User menu in header
- Logout buttons
- Profile links
- Clerk auth containers

Modify `codenet.css` to customize the appearance.

## Troubleshooting

### Common Issues:
1. **Clerk not loading**: Check that your publishable key and domain are correct
2. **Authentication not working**: Verify backend JWT validation is configured
3. **Redirects not working**: Ensure file paths are correct in your setup
4. **CORS errors**: Make sure your Spring Boot CORS configuration includes your frontend domain

### Debug Tips:
- Check browser console for Clerk initialization errors
- Verify network requests are reaching your backend
- Test authentication endpoints directly with Postman
- Check MongoDB for user synchronization
