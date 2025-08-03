package com.codenet.codenetbackend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collections;

@Component
@Slf4j
public class ClerkAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            try {
                // In a real implementation, you would validate the JWT token with Clerk
                // For now, we'll extract the user ID from the token (simplified)
                String userId = extractUserIdFromToken(token);
                
                if (userId != null) {
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    // Add user ID as request attribute for controllers
                    request.setAttribute("clerkUserId", userId);
                }
            } catch (Exception e) {
                log.error("Error processing authentication token: {}", e.getMessage());
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String extractUserIdFromToken(String token) {
        // This is a simplified implementation
        // In production, you should validate the JWT token with Clerk's public key
        // For now, we'll assume the token contains the user ID
        try {
            // Simple token parsing - in reality, you'd use a JWT library
            if (token.length() > 10) {
                return token.substring(0, 10); // Simplified user ID extraction
            }
        } catch (Exception e) {
            log.error("Error extracting user ID from token: {}", e.getMessage());
        }
        return null;
    }
} 