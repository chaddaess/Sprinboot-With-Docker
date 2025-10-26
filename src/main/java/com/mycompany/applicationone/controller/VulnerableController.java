package com.mycompany.applicationone.controller;

import org.springframework.web.bind.annotation.*;
import java.sql.*;
import java.util.Random;

@RestController
@RequestMapping("/api/v1/vulnerable")
public class VulnerableController {

    // VULNERABILITY 1: Hardcoded credentials (Critical)
    private static final String DB_PASSWORD = "MySecretPassword123!";
    private static final String API_KEY = "sk-1234567890abcdef";
    
    // VULNERABILITY 2: Weak cryptography (Major)
    private static final Random random = new Random();

    // VULNERABILITY 3: SQL Injection vulnerability (Critical)
    @GetMapping("/user/{username}")
    public String getUserData(@PathVariable String username) {
        String query = "SELECT * FROM users WHERE username = '" + username + "'";
        // This concatenation makes it vulnerable to SQL injection
        return executeQuery(query);
    }

    // VULNERABILITY 4: Command Injection vulnerability (Critical)
    @GetMapping("/execute")
    public String executeCommand(@RequestParam String cmd) throws Exception {
        // Dangerous: executing user input directly
        Process process = Runtime.getRuntime().exec(cmd);
        return "Command executed";
    }

    // VULNERABILITY 5: Path Traversal vulnerability (Critical)
    @GetMapping("/file")
    public String readFile(@RequestParam String filename) {
        // No validation - allows reading any file
        String path = "/var/data/" + filename;
        return "Reading file: " + path;
    }

    // VULNERABILITY 6: Insecure Random (Major)
    @GetMapping("/token")
    public String generateToken() {
        // Using Random instead of SecureRandom for security-sensitive operation
        int token = random.nextInt(999999);
        return "Token: " + String.format("%06d", token);
    }

    // VULNERABILITY 7: Empty catch block (Major)
    @GetMapping("/process")
    public String processData(@RequestParam String data) {
        try {
            // Some processing
            if (data.length() > 100) {
                throw new IllegalArgumentException("Data too long");
            }
        } catch (Exception e) {
            // Empty catch - vulnerability
        }
        return "Processed";
    }

    // VULNERABILITY 8: Resource leak (Major)
    @GetMapping("/database")
    public String queryDatabase() {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/mydb",
                "root",
                DB_PASSWORD  // Using hardcoded password
            );
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users");
            // Resource leak - rs, stmt, conn not closed properly
            return "Query executed";
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
        // Missing finally block to close resources
    }

    // VULNERABILITY 9: Information exposure (Major)
    @GetMapping("/error")
    public String causeError() {
        try {
            throw new Exception("Database connection failed: server=192.168.1.100, user=admin, db=production");
        } catch (Exception e) {
            // Exposing sensitive information in error messages
            return "Error occurred: " + e.getMessage();
        }
    }

    // VULNERABILITY 10: Null pointer dereference (Major)
    @GetMapping("/data")
    public String getData(@RequestParam(required = false) String input) {
        // No null check - will throw NullPointerException
        return input.toUpperCase();
    }

    // Helper method (still vulnerable)
    private String executeQuery(String query) {
        return "Executing: " + query;
    }
}
