import ballerina/http;
import ballerina/io;
import ballerinax/mongodb;

// MongoDB client initialization for 5.1.0
mongodb:Client mongoClient = check new ({ connection: "mongodb://localhost:27017" });
const string DB_NAME = "Codenet";
const string USERS_COLLECTION = "users";
const string PROJECTS_COLLECTION = "projects";

// Service to serve static frontend files
service / on new http:Listener(8080) {

    resource function get staticFiles(http:Caller caller, http:Request req) returns error? {
        // Serve static files from the frontend directory (fallback for Ballerina 2.x)
        string path = req.rawPath;
        if path == "/" {
            path = "/codenet.html";
        }
        string filePath = "../frontend" + path;
        byte[]|error fileContent = io:fileReadBytes(filePath);
        if fileContent is byte[] {
            http:Response res = new;
            res.setPayload(fileContent);
            // Set content-type based on file extension (basic)
            if filePath.endsWith(".html") {
                res.setHeader("content-type", "text/html");
            } else if filePath.endsWith(".css") {
                res.setHeader("content-type", "text/css");
            } else if filePath.endsWith(".js") {
                res.setHeader("content-type", "application/javascript");
            } else if filePath.endsWith(".png") {
                res.setHeader("content-type", "image/png");
            } else if filePath.endsWith(".jpg") || filePath.endsWith(".jpeg") {
                res.setHeader("content-type", "image/jpeg");
            } else if filePath.endsWith(".webp") {
                res.setHeader("content-type", "image/webp");
            } else if filePath.endsWith(".svg") {
                res.setHeader("content-type", "image/svg+xml");
            }
            check caller->respond(res);
        } else {
            check caller->respond("File not found");
        }
    }


    // Login endpoint
    resource function post login(http:Caller caller, http:Request req) returns error? {
        check caller->respond({ success: false, message: "MongoDB connector 5.1.0 does not support direct CRUD operations from the client object. Please use getDatabase() to get a database object, then use collection-level methods for CRUD." });
    }


    // Signup endpoint
    resource function post signup(http:Caller caller, http:Request req) returns error? {
        check caller->respond({ success: false, message: "MongoDB connector 5.1.0 does not support direct CRUD operations from the client object. Please use getDatabase() to get a database object, then use collection-level methods for CRUD." });
    }


    // Project submission endpoint
    resource function post submitProject(http:Caller caller, http:Request req) returns error? {
        check caller->respond({ success: false, message: "MongoDB connector 5.1.0 does not support direct CRUD operations from the client object. Please use getDatabase() to get a database object, then use collection-level methods for CRUD." });
    }
}