package com.codenet.codenetbackend.service;

import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class JwtValidationService {
    private static final Logger log = LoggerFactory.getLogger(JwtValidationService.class);

    @Value("${clerk.jwt-issuer}")
    private String clerkJwksUrl;

    private JwkProvider jwkProvider;

    public JwtValidationService() {
        // Initialize JWK provider
        try {
            this.jwkProvider = new JwkProviderBuilder(new URL(clerkJwksUrl))
                    .cached(10, 24, TimeUnit.HOURS) // Cache JWKs for 24 hours
                    .rateLimited(10, 1, TimeUnit.MINUTES) // 10 requests per minute
                    .build();
        } catch (Exception e) {
            log.error("Failed to initialize JWK provider", e);
        }
    }

    public String validateTokenAndGetUserId(String token) throws Exception {
        if (jwkProvider == null) {
            throw new RuntimeException("JWK provider not initialized");
        }

        DecodedJWT jwt = JWT.decode(token);
        
        // Get the public key from JWKS
        Algorithm algorithm = Algorithm.RSA256((java.security.interfaces.RSAPublicKey) jwkProvider.get(jwt.getKeyId()).getPublicKey(), null);
        
        // Verify signature
        algorithm.verify(jwt);

        // Check token expiration
        if (jwt.getExpiresAt().before(new Date())) {
            throw new RuntimeException("JWT token has expired");
        }

        // Get user ID from subject claim
        String userId = jwt.getSubject();
        if (userId == null) {
            throw new RuntimeException("JWT token missing subject claim");
        }

        log.debug("JWT validation successful for user: {}", userId);
        return userId;
    }

    public boolean isValidToken(String token) {
        try {
            validateTokenAndGetUserId(token);
            return true;
        } catch (Exception e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }
} 