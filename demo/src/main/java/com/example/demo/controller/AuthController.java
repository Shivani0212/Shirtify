package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.payload.AuthRequest;
import com.example.demo.payload.JwtResponse;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CrossOrigin(origins = "*") // Development only
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    // Signup endpoint
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody AuthRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody AuthRequest request) {
        try {
            logger.info("Attempting login for user: {}", request.getUsername());

            // Authenticate the user
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            // Load user details from the database
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

            // Generate a JWT token
            String token = jwtUtil.generateToken(userDetails.getUsername());

            logger.info("Login successful for user: {}", request.getUsername());

            // Return the token inside a JwtResponse object
            return ResponseEntity.ok(new JwtResponse(token));

        } catch (BadCredentialsException e) {
            logger.error("Authentication failed for user: {}. Invalid credentials.", request.getUsername());
            return ResponseEntity.status(401).body(new JwtResponse("Invalid username or password"));
        } catch (InternalAuthenticationServiceException e) {
            logger.error("Authentication service error for user: {}", request.getUsername());
            return ResponseEntity.status(500).body(new JwtResponse("Authentication service error"));
        } catch (Exception e) {
            logger.error("Unexpected error occurred during authentication for user: {}", request.getUsername(), e);
            return ResponseEntity.status(500).body(new JwtResponse("Authentication failed"));
        }
    }
}
