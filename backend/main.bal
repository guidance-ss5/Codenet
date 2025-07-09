import ballerina/http;
import ballerina/log;
import ballerinax/mongodb;
import ballerina/crypto; 

// Import our custom data models
import guidance-ss5/Codenet/backend.models; 

// --- Database Configuration ---
// The connection string for your local MongoDB instance.
const string MONGO_URL = "mongodb://localhost:27017";
// The name of our database.
const string DB_NAME = "codenet";

// --- Global MongoDB Client ---
final mongodb:Client mongoClient = check new (MONGO_URL);
// Get a handle to our specific database.
final mongodb:Database codenetDb = mongoClient->getDatabase(DB_NAME);
// Get handles to our collections.
final mongodb:Collection userCollection = codenetDb->getCollection("users");
final mongodb:Collection projectCollection = codenetDb->getCollection("projects");


// --- HTTP Service ---
@http:ServiceConfig {
    cors: {
        allowOrigins: ["http://localhost:8000", "http://127.0.0.1:5500"], // Add your frontend origins
        allowMethods: ["GET", "POST", "PUT", "DELETE", "OPTIONS"],
        allowHeaders: ["Content-Type", "Authorization"]
    }
}
service / on new http:Listener(9090) {
     resource function post signup(@http:Payload json payload) returns http:Created|http:Conflict|http:InternalServerError {
        
        // 1. Extract and validate user data from the incoming JSON payload.
        string|error username = payload.username.fromJsonString();
        string|error email = payload.email.fromJsonString();
        string|error password = payload.password.fromJsonString();

        if username is error || email is error || password is error {
            log:printError("Invalid signup payload", payload);
            return http:CONFLICT;
        }

        // 2. Check if a user with the same username or email already exists.
        // We create a query document to find a match.
        map<anydata> query = {
            "$or": [
                {"username": username},
                {"email": email}
            ]
        };

        // Execute the query.
        var findResult = userCollection->findOne(query);

        if findResult is mongodb:Document {
            // If a document is found, it means the user already exists.
            log:printWarn("Signup attempt for existing user", {username, email});
            return http:CONFLICT;
        } else if findResult is error {
            // Handle potential database errors.
            log:printError("Error finding user", findResult);
            return http:INTERNAL_SERVER_ERROR;
        }

        // 3. Hash the password for secure storage. NEVER store plain text passwords.
        // crypto:hashSha256 is a good, simple choice for this example.
        string|error passwordHash = crypto:hashSha256(password);
        if passwordHash is error {
            log:printError("Failed to hash password", passwordHash);
            return http:INTERNAL_SERVER_ERROR;
        }

        // 4. Create the new User record.
        models:User newUser = {
            username: username,
            email: email,
            passwordHash: passwordHash
        };

        // 5. Insert the new user document into the 'users' collection.
        var insertResult = userCollection->insertOne(newUser);

        if insertResult is models:MongoId {
            // Success! The user was created.
            log:printInfo("New user created", {username, id: insertResult});
            // Return a 201 Created status to the client.
            return http:CREATED;
        } else {
            // Handle potential database insertion errors.
            log:printError("Failed to insert new user", insertResult);
            return http:INTERNAL_SERVER_ERROR;
        }
    }
    
}