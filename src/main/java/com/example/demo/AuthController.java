package com.example.demo;

import com.example.demo.security.JwtUtils; // Double-check this path matches your security folder layout
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // From your SecurityConfig class

    @Autowired
    private JwtUtils jwtUtils; // From your security folder

    // 1. UPDATED POST METHOD: Hashes password securely before saving
    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return "Error: Username is already taken!";
        }
        
        // Securely scramble the raw password using BCrypt before writing to Aiven MySQL
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        
        return "SUCCESS: User " + user.getUsername() + " saved with secure password hashing!";
    }

    // 2. NEW LOGIN ENDPOINT: Validates credentials and returns JWT
    @PostMapping("/login")
    public String loginUser(@RequestBody LoginRequest loginRequest) {
        // Look up user safely via Optional container
        Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());

        if (userOpt.isEmpty()) {
            return "Error: Invalid username or password!";
        }

        User user = userOpt.get();

        // Cryptographically compare raw password against the database hash string
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return "Error: Invalid username or password!";
        }

        // Generate and hand over the session token
        String token = jwtUtils.generateToken(user.getUsername());
        return "Login Successful! Your JWT Token: " + token;
    }

    @GetMapping("/user/{username}")
    public User getUserByUsername(@PathVariable String username) {
        return userRepository.findByUsername(username).orElse(new User());
    }
}
