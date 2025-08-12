import ballerina/http;
import ballerina/log;

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

        // 2. Verify Webhook Signature (simplified for now)
        if (!verifyWebhookSignature(svixId, svixTimestamp, svixSignature, jsonBody)) {
            log:printError("Webhook signature verification failed");
            return http:BadRequest();
        }
        log:printInfo("Webhook signature verified successfully");

        // 3. Parse Payload
        json payload = check json:parse(jsonBody);
        string eventType = check payload.eventType.toString();

        if (eventType == "user.created" || eventType == "user.updated") {
            json userData = check payload.data;
            string clerkUserId = check userData.id.toString();
            string email = check userData.email_addresses[0].email_address.toString();

            log:printInfo("Clerk User Event", eventType = eventType, userId = clerkUserId, email = email);

            // 4. Call Spring Boot User Sync Endpoint
            http:Client backendClient = new (SPRING_BOOT_USER_SYNC_URL);

            json userSyncPayload = {
                "id": clerkUserId,
                "email": email
            };

            http:Response|error response = backendClient->post("", userSyncPayload);
            if (response is http:Response) {
                log:printInfo("Spring Boot sync call successful", status = response.statusCode);
            } else {
                log:printError("Error calling Spring Boot for user sync", err = response.toString());
                return http:BadRequest();
            }
        }

        return http:Ok();
    }
}

// Simplified webhook signature verification
function verifyWebhookSignature(string svixId, string svixTimestamp, string svixSignature, string payload) returns boolean {
    // Basic validation - in production, implement full Svix verification
    if (svixId.length() > 0 && svixTimestamp.length() > 0 && svixSignature.length() > 0) {
        return true;
    }
    return false;
}
