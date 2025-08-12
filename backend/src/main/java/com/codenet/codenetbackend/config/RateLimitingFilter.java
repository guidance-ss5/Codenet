package com.codenet.codenetbackend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {
    
    // Simple rate limiting structure
    private static class RateLimit {
        private final AtomicInteger count = new AtomicInteger(0);
        private final AtomicLong lastResetTime = new AtomicLong(System.currentTimeMillis());
        private final int maxRequests;
        private final long windowMs;
        
        public RateLimit(int maxRequests, long windowMs) {
            this.maxRequests = maxRequests;
            this.windowMs = windowMs;
        }
        
        public boolean isAllowed() {
            long now = System.currentTimeMillis();
            long lastReset = lastResetTime.get();
            
            // Reset counter if window has passed
            if (now - lastReset > windowMs) {
                if (lastResetTime.compareAndSet(lastReset, now)) {
                    count.set(0);
                }
            }
            
            return count.incrementAndGet() <= maxRequests;
        }
    }
    
    private final ConcurrentHashMap<String, RateLimit> clientLimits = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String clientIp = getClientIpAddress(request);
        String endpoint = request.getRequestURI();
        
        // Different rate limits for different endpoints
        RateLimit rateLimit = getRateLimitForClient(clientIp, endpoint);
        
        if (rateLimit.isAllowed()) {
            // Request allowed
            filterChain.doFilter(request, response);
        } else {
            // Rate limit exceeded
            response.setStatus(429); // Too Many Requests
            response.setContentType("application/json");
            response.getWriter().write(
                "{\"error\":\"Too many requests\",\"message\":\"Rate limit exceeded. Please try again later.\",\"code\":\"RATE_LIMIT_EXCEEDED\"}"
            );
        }
    }

    private RateLimit getRateLimitForClient(String clientIp, String endpoint) {
        String key = clientIp + ":" + getRateLimitKey(endpoint);
        
        return clientLimits.computeIfAbsent(key, k -> {
            if (endpoint.contains("upload")) {
                // File upload endpoints: 5 requests per minute
                return new RateLimit(5, 60000);
            } else if (endpoint.contains("like")) {
                // Like endpoints: 20 requests per minute
                return new RateLimit(20, 60000);
            } else if (endpoint.startsWith("/api/projects")) {
                // General project endpoints: 100 requests per minute
                return new RateLimit(100, 60000);
            } else if (endpoint.startsWith("/api/users")) {
                // User endpoints: 50 requests per minute
                return new RateLimit(50, 60000);
            } else {
                // Default: 200 requests per minute
                return new RateLimit(200, 60000);
            }
        });
    }

    private String getRateLimitKey(String endpoint) {
        if (endpoint.contains("upload")) return "upload";
        if (endpoint.contains("like")) return "like";
        if (endpoint.startsWith("/api/projects")) return "projects";
        if (endpoint.startsWith("/api/users")) return "users";
        return "general";
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader != null && !xForwardedForHeader.isEmpty()) {
            return xForwardedForHeader.split(",")[0].trim();
        }
        
        String xRealIpHeader = request.getHeader("X-Real-IP");
        if (xRealIpHeader != null && !xRealIpHeader.isEmpty()) {
            return xRealIpHeader;
        }
        
        return request.getRemoteAddr();
    }
}
