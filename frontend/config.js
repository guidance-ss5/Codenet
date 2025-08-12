// Environment Configuration for Frontend
class EnvironmentConfig {
    constructor() {
        // Detect environment based on hostname
        this.isDevelopment = window.location.hostname === 'localhost' || 
                           window.location.hostname === '127.0.0.1' ||
                           window.location.hostname.includes('dev');
        
        this.isProduction = !this.isDevelopment;
    }

    // API Configuration
    getApiBaseUrl() {
        if (this.isDevelopment) {
            return 'http://localhost:8080/api';
        } else {
            // Production - use relative path or your domain
            return `${window.location.protocol}//${window.location.host}/api`;
        }
    }

    // Clerk Configuration
    getClerkPublishableKey() {
        if (this.isDevelopment) {
            return 'pk_test_aGFuZHktaGVycmluZy0xMy5jbGVyay5hY2NvdW50cy5kZXYk';
        } else {
            // Production key should be set via environment variable
            return window.CLERK_PUBLISHABLE_KEY || 'your_production_clerk_key';
        }
    }

    // Logging Configuration
    shouldLogDebug() {
        return this.isDevelopment;
    }

    // Feature Flags
    getFeatureFlags() {
        return {
            enableAdvancedSearch: this.isProduction,
            enableFilePreview: true,
            enableNotifications: this.isProduction,
            maxFileSize: this.isDevelopment ? 10 * 1024 * 1024 : 5 * 1024 * 1024, // 10MB dev, 5MB prod
            maxFilesPerProject: 10
        };
    }

    // Error Reporting
    shouldReportErrors() {
        return this.isProduction;
    }

    // Performance Monitoring
    shouldTrackPerformance() {
        return this.isProduction;
    }
}

// Global configuration instance
const config = new EnvironmentConfig();

// Export for use in other files
if (typeof module !== 'undefined' && module.exports) {
    module.exports = config;
} else {
    window.AppConfig = config;
}
