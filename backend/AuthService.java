import ballerina/http;

// Import our new handler modules
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
    
    resource function post api/signup(http:PayloadContext payload) returns http:Created|http:Conflict|http:InternalServerError {
        return auth_handler:handleSignup(payload);
    }

    resource function post api/login(http:PayloadContext payload) returns json|http:Unauthorized|http:InternalServerError {
        return auth_handler:handleLogin(payload);
    }
    
    // --- Static File Serving Resource ---
    // This catch-all resource will handle all other GET requests.
    resource function get . (http:Request req) returns http:Response|http:NotFound|error {
        return file_handler:serveStaticFile(req);
    }
}