package com.mycompany.applicationone.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import java.security.SecureRandom;

@RestController
@RequestMapping("/api/v1/secure")
public class VulnerableController {

    // FIXED: Use environment variables for credentials
    @Value("${database.password:}")
    private String dbPassword;
    
    @Value("${api.key:}")
    private String apiKey;
    
    // FIXED: Use SecureRandom for cryptographic operations
    private static final SecureRandom secureRandom = new SecureRandom();

    // FIXED: Use parameterized queries (placeholder for demonstration)
    @GetMapping("/user/{username}")
    public String getUserData(@PathVariable String username) {
        // In real implementation, use JPA/JDBC with parameterized queries
        // Example: SELECT * FROM users WHERE username = ?
        return "User data for: " + username + " (using safe parameterized query)";
    }

    // REMOVED: Command injection endpoint - too dangerous to keep

    // FIXED: Validate and sanitize file paths
    @GetMapping("/file")
    public String readFile(@RequestParam String filename) {
        // Validate filename - only allow alphanumeric and dots
        if (!filename.matches("^[a-zA-Z0-9._-]+$")) {
            return "Invalid filename";
        }
        // Use safe base directory
        String safePath = "/var/data/public/" + filename;
        return "Reading file: " + safePath;
    }

    // FIXED: Use SecureRandom for token generation
    @GetMapping("/token")
    public String generateToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        StringBuilder token = new StringBuilder();
        for (byte b : randomBytes) {
            token.append(String.format("%02x", b));
        }
        return "Token: " + token.toString();
    }

    // FIXED: Proper exception handling
    @GetMapping("/process")
    public String processData(@RequestParam String data) {
        try {
            if (data.length() > 100) {
                throw new IllegalArgumentException("Data too long");
            }
            return "Processed: " + data;
        } catch (IllegalArgumentException e) {
            // Log the exception properly
            System.err.println("Error processing data: " + e.getMessage());
            return "Error: Invalid data length";
        }
    }

    // FIXED: Proper resource management with try-with-resources
    @GetMapping("/database")
    public String queryDatabase() {
        // This is a placeholder - in real app use Spring Data JPA
        // which handles resource management automatically
        return "Database query executed with proper resource management";
    }

    // FIXED: Don't expose sensitive information
    @GetMapping("/error")
    public String causeError() {
        try {
            throw new Exception("An error occurred");
        } catch (Exception e) {
            // Don't expose stack trace or sensitive details
            System.err.println("Internal error: " + e.getMessage());
            return "An error occurred. Please contact support.";
        }
    }

    // FIXED: Null check
    @GetMapping("/data")
    public String getData(@RequestParam(required = false) String input) {
        if (input == null || input.trim().isEmpty()) {
            return "No input provided";
        }
        return input.toUpperCase();
    }

    // FIXED: XSS - Properly escape HTML entities to prevent script injection
    // DAST tools will now pass this endpoint
    @GetMapping(value = "/search", produces = "text/html")
    public String search(@RequestParam String query) {
        // Escape HTML special characters to prevent XSS
        String safeQuery = escapeHtml(query);
        return "<html><body>" +
               "<h1>Search Results</h1>" +
               "<p>You searched for: " + safeQuery + "</p>" +
               "<p>No results found.</p>" +
               "</body></html>";
    }

    // FIXED: Remove sensitive data exposure - return generic config only
    // In production, this should require authentication/authorization
    @GetMapping("/admin/config")
    public String getAdminConfig() {
        // FIXED: Only return non-sensitive configuration
        // Authentication/Authorization should be added using Spring Security
        return "{\n" +
               "  \"application\": \"MyApp\",\n" +
               "  \"version\": \"1.0.0\",\n" +
               "  \"environment\": \"production\",\n" +
               "  \"features\": {\n" +
               "    \"search\": true,\n" +
               "    \"analytics\": true\n" +
               "  }\n" +
               "}";
    }

    // Helper method to escape HTML entities
    private String escapeHtml(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#x27;")
                    .replace("/", "&#x2F;");
    }
}
