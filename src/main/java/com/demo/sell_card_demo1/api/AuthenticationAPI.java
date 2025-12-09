package com.demo.sell_card_demo1.api;

import com.demo.sell_card_demo1.dto.AccountResponse;
import com.demo.sell_card_demo1.dto.LoginRequest;
import com.demo.sell_card_demo1.dto.RegisterRequest;
import com.demo.sell_card_demo1.entity.User;
import com.demo.sell_card_demo1.service.AuthenticationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthenticationAPI {
    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterRequest user) {
        User newUser = authenticationService.register(user);
        return ResponseEntity.ok("Register successfully");
    }

    @PostMapping("/login")
    public AccountResponse login(@RequestBody LoginRequest loginRequest) {
        AccountResponse response = authenticationService.login(loginRequest);
        return response;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        authenticationService.forgotPassword(email);
        return ResponseEntity.ok("Send email successfully");
    }

    @SecurityRequirement(
            name = "api"
    )
    @PostMapping("/reset-password")
    public ResponseEntity resetPassword(@RequestParam String password) {
        authenticationService.resetPassword(password);
        return ResponseEntity.ok("Reset password successfully");
    }
}
