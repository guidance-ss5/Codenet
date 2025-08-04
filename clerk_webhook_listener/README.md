# Clerk Webhook Listener (Ballerina Service)

This Ballerina service handles webhooks from Clerk for user synchronization with your Spring Boot backend.

## Features

- Receives webhooks from Clerk for user events (created, updated, deleted)
- Verifies webhook signatures (placeholder implementation)
- Syncs user data with Spring Boot backend
- Handles user creation, updates, and deletion

## Setup

### 1. Get Your Clerk Webhook Secret

1. Go to your [Clerk Dashboard](https://dashboard.clerk.com/)
2. Navigate to **Webhooks** in the sidebar
3. Create a new webhook endpoint
4. Copy the webhook secret (starts with `whsec_`)

### 2. Configure the Service

Edit `Config.toml` and update the following values:

```toml
# Replace with your actual Clerk webhook secret
CLERK_WEBHOOK_SECRET = "whsec_YOUR_ACTUAL_WEBHOOK_SECRET"

# Update if your Spring Boot backend runs on a different port
SPRING_BOOT_USER_SYNC_URL = "http://localhost:8080/api/users/sync"
```

### 3. Configure Clerk Webhooks

In your Clerk Dashboard:

1. Go to **Webhooks**
2. Add a new endpoint with URL: `https://your-domain.com/clerk-webhook`
3. Select the following events:
   - `user.created`
   - `user.updated` 
   - `user.deleted`
4. Save the webhook configuration

### 4. Run the Service

```bash
# Build the service
bal build

# Run the service
bal run
```

The service will start on port 9090.

## API Endpoints

- `POST /clerk-webhook` - Receives webhooks from Clerk

## Security Notes

⚠️ **IMPORTANT**: The current webhook signature verification is a placeholder. For production:

1. Implement proper Svix signature verification
2. Use environment variables for sensitive configuration
3. Add rate limiting
4. Add proper error handling and logging

## Integration with Spring Boot

This service calls your Spring Boot backend at:
- `POST /api/users/sync` - For user creation/updates
- `DELETE /api/users/sync/{userId}` - For user deletion

Make sure your Spring Boot backend has these endpoints implemented.

## Development

### Local Testing

You can test the webhook locally using tools like ngrok:

```bash
# Install ngrok
# Then expose your local service
ngrok http 9090

# Use the ngrok URL in your Clerk webhook configuration
```

### Logs

The service logs all webhook events and API calls. Check the console output for debugging.

## Production Deployment

1. Deploy to a cloud platform (AWS, Azure, GCP)
2. Update the webhook URL in Clerk Dashboard
3. Set environment variables for configuration
4. Implement proper webhook signature verification
5. Add monitoring and health checks 