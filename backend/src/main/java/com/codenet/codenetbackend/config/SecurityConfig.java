package com.codenet.codenetbackend.config;

import com.codenet.codenetbackend.service.JwtValidationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    private final JwtValidationService jwtValidationService;

    public SecurityConfig(JwtValidationService jwtValidationService) {
        this.jwtValidationService = jwtValidationService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless APIs
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Security Headers (Updated for Spring Security 6.1+)
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.deny())
                .contentTypeOptions(Customizer.withDefaults())
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000)
                    .includeSubDomains(true)
                    .preload(true)
                )
            )
            
            .authorizeHttpRequests(authorize -> authorize
                // Public endpoints (no authentication needed)
                .requestMatchers(HttpMethod.GET, "/api/projects/search/**", "/api/projects/trending", "/api/projects/stats").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users/sync").permitAll() // For Ballerina webhook
                .requestMatchers(HttpMethod.DELETE, "/api/users/sync/**").permitAll() // For Ballerina webhook
                
                // Health check endpoints
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                
                // Static resources and uploads
                .requestMatchers("/uploads/**", "/static/**", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/error").permitAll()
                
                // Admin endpoints (require authentication and admin role)
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // Protected endpoints (require authentication)
                .requestMatchers(HttpMethod.POST, "/api/projects/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/projects/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/projects/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/projects/my").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/projects/*/like").authenticated()
                
                // All other API endpoints require authentication
                .requestMatchers("/api/**").authenticated()
                
                // Allow frontend static files
                .anyRequest().permitAll()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(clerkJwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public OncePerRequestFilter clerkJwtAuthenticationFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
                String authHeader = request.getHeader("Authorization");

                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    try {
                        String clerkUserId = jwtValidationService.validateTokenAndGetUserId(token);
                        
                        // Store Clerk user ID in request attributes for controllers to use
                        request.setAttribute("clerkUserId", clerkUserId);

                        // Create authentication token
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                clerkUserId, null, Collections.singletonList(new SimpleGrantedAuthority("USER")));
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                    } catch (Exception e) {
                        log.error("JWT authentication failed: {}", e.getMessage());
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        return;
                    }
                }
                filterChain.doFilter(request, response);
            }
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
            "http://127.0.0.1:5500", 
            "http://localhost:5500",
            "http://localhost:8080",
            "http://127.0.0.1:8080"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
} 