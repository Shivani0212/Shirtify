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
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.BadCredentialsException;

@RestController
@RequestMapping("/auth")
public class AuthController {

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
    public String signup(@RequestBody AuthRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return "Username already exists";
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        return "User registered successfully";
    }

    // Login endpoint
    @PostMapping("/login")
    public JwtResponse login(@RequestBody AuthRequest request) {
        try {
            // Authenticate the user
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            // Load user details from the database
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

            // Generate a JWT token
            String token = jwtUtil.generateToken(userDetails.getUsername());

            // Return the token inside a JwtResponse object
            return new JwtResponse(token);

        } catch (BadCredentialsException e) {
            // Return a specific error message for invalid credentials
            throw new RuntimeException("Invalid username or password", e);
        } catch (Exception e) {
            // Catch any other exceptions and return a generic error message
            throw new RuntimeException("Authentication failed", e);
        }
    }
}
