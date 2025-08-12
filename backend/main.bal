import ballerina/http;
import ballerina/log;

service / on new http:Listener(9090) {
    resource function get health() returns string {
        return "Webhook service is running";
    }
}
