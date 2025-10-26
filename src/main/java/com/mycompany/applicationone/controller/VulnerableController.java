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

    // VULNERABLE: XSS - Reflects user input without sanitization in HTML response
    // DAST tools like OWASP ZAP will detect this
    @GetMapping(value = "/search", produces = "text/html")
    public String search(@RequestParam String query) {
        // VULNERABILITY: Directly embedding user input in HTML without escaping
        // An attacker could inject: <script>alert('XSS')</script>
        return "<html><body>" +
               "<h1>Search Results</h1>" +
               "<p>You searched for: " + query + "</p>" +
               "<p>No results found.</p>" +
               "</body></html>";
    }

    // VULNERABLE: Sensitive data exposure without authentication
    // DAST will flag this as accessible without proper security headers
    @GetMapping("/admin/config")
    public String getAdminConfig() {
        // VULNERABILITY: Exposing sensitive configuration without authentication
        return "{\n" +
               "  \"database\": \"postgresql://prod-db:5432/myapp\",\n" +
               "  \"api_keys\": {\n" +
               "    \"payment_gateway\": \"pk_live_123456789\",\n" +
               "    \"email_service\": \"key-abcdef123456\"\n" +
               "  },\n" +
               "  \"internal_endpoints\": [\n" +
               "    \"http://internal-api.local/admin\",\n" +
               "    \"http://10.0.0.5:8080/metrics\"\n" +
               "  ]\n" +
               "}";
    }
}
