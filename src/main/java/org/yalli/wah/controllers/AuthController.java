package org.yalli.wah.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.yalli.wah.dtos.LoginDto;
import org.yalli.wah.dtos.RegisterDto;
import org.yalli.wah.models.User;
import org.yalli.wah.payloads.ApiResponse;
import jakarta.validation.Valid;
import org.yalli.wah.services.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterDto registerDto) {
        if (userService.register(registerDto) == null) {
            return ResponseEntity.badRequest().body(new ApiResponse("Email already exists or password mismatch", false));
        }
        return ResponseEntity.ok(new ApiResponse("User registered successfully. Check your email for confirmation link.", true));
    }

    @GetMapping("/confirm")
    public ResponseEntity<ApiResponse> confirmEmail(@RequestParam String email, @RequestParam String token) {
        boolean isConfirmed = userService.confirmEmail(email, token);
        if (!isConfirmed) {
            return ResponseEntity.badRequest().body(new ApiResponse("Invalid confirmation link", false));
        }
        return ResponseEntity.ok(new ApiResponse("Email confirmed successfully", true));
    }
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(loginDto.getEmail());



            return ResponseEntity.ok().body(new ApiResponse("Login successful for user: " + userDetails.getUsername(), true));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(new ApiResponse("Invalid email or password", false));
        }
    }
}



