// In AuthService.bal
import ballerina/http;

// These imports will now work because of the correct folder structure
import guidance555codenet.backend.auth_handler;
import guidance555codenet.backend.file_handler;

@http:ServiceConfig {
    cors: {
        allowOrigins: ["*"],
        allowMethods: ["GET", "POST", "OPTIONS"],
        allowHeaders: ["Content-Type", "Authorization"]
    }
}
service / on new http:Listener(9090) {

    // --- API Resources ---
    
    resource function post api/signup(json payload) returns http:Created|http:Conflict|http:InternalServerError {
        return auth_handler:handleSignup(payload);
    }

    resource function post api/login(json payload) returns json|http:Unauthorized|http:InternalServerError {
        return auth_handler:handleLogin(payload);
    }
    
    // --- Static File Serving Resource ---
    // CORRECTED: Use [string... path] for a catch-all resource
    resource function get [string... path](http:Request req) returns http:Response|http:NotFound|error {
        return file_handler:serveStaticFile(req);
    }
}