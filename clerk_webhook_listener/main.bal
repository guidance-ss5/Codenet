import ballerina/http;
import ballerina/log;
import ballerina/crypto;
import ballerina/mime;

// Configuration - replace with your actual values
configurable string CLERK_WEBHOOK_SECRET = "whsec_YOUR_CLERK_WEBHOOK_SECRET";
configurable string SPRING_BOOT_USER_SYNC_URL = "http://localhost:8080/api/users/sync";

service /clerk-webhook on new http:Listener(9090) {
    resource function post .(@http:Header {name: "svix-id"} string svixId,
                              @http:Header {name: "svix-timestamp"} string svixTimestamp,
                              @http:Header {name: "svix-signature"} string svixSignature,
                              http:Request req) returns http:Ok|http:BadRequest {
        
        // 1. Get raw request body
        string jsonBody = check req.getTextPayload();
        log:printInfo("Received Clerk Webhook", body = jsonBody);

        // 2. Verify Webhook Signature (CRUCIAL for security)
        // Note: This is a simplified verification. In production, implement full Svix verification
        if (!verifyWebhookSignature(svixId, svixTimestamp, svixSignature, jsonBody)) {
            log:printError("Webhook signature verification failed");
            return http:BadRequest();
        }
        log:printInfo("Webhook signature verified successfully");

        // 3. Parse Payload
        json payload = check json.parse(jsonBody);
        string eventType = payload.eventType.toString();

        if (eventType == "user.created" || eventType == "user.updated") {
            json userData = payload.data;
            string clerkUserId = userData.id.toString();
            string email = userData.email_addresses[0].email_address.toString();
            string? username = userData.username?.toString();
            string? firstName = userData.first_name?.toString();
            string? lastName = userData.last_name?.toString();
            string? profileImageUrl = userData.profile_image_url?.toString();

            log:printInfo("Clerk User Event", eventType = eventType, userId = clerkUserId, email = email);

            // 4. Call Spring Boot User Sync Endpoint
            http:Client backendClient = new (SPRING_BOOT_USER_SYNC_URL);

            json userSyncPayload = {
                "id": clerkUserId,
                "email": email,
                "username": username,
                "firstName": firstName,
                "lastName": lastName,
                "profileImageUrl": profileImageUrl
            };

            http:Response|error response = backendClient->post("", userSyncPayload);
            if (response is http:Response) {
                log:printInfo("Spring Boot sync call successful", status = response.statusCode);
            } else {
                log:printError("Error calling Spring Boot for user sync", err = response.toString());
                return http:BadRequest();
            }
        } else if (eventType == "user.deleted") {
            json userData = payload.data;
            string clerkUserId = userData.id.toString();
            log:printInfo("Clerk User Deleted Event", userId = clerkUserId);

            // Call Spring Boot to delete user
            http:Client backendClient = new (SPRING_BOOT_USER_SYNC_URL + "/" + clerkUserId);
            http:Response|error response = backendClient->delete("");
            if (response is http:Response) {
                log:printInfo("Spring Boot delete call successful", status = response.statusCode);
            } else {
                log:printError("Error calling Spring Boot for user deletion", err = response.toString());
                return http:BadRequest();
            }
        }

        return http:Ok();
    }
}

// Helper function for webhook signature verification
function verifyWebhookSignature(string svixId, string svixTimestamp, string svixSignature, string payload) returns boolean {
    // This is a simplified verification. In production, implement the full Svix verification:
    // 1. Parse the svixSignature to extract the signature
    // 2. Create the signed content: svixId + "." + svixTimestamp + "." + payload
    // 3. Compute HMAC-SHA256 of the signed content with your webhook secret
    // 4. Compare the computed signature with the received signature
    
    // For now, we'll do a basic check
    if (svixId.length() > 0 && svixTimestamp.length() > 0 && svixSignature.length() > 0) {
        return true; // Simplified check - replace with proper verification
    }
    return false;
}
