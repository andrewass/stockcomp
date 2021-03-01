package com.stockcomp.controller;

import com.stockcomp.request.AuthenticationRequest;
import com.stockcomp.request.SignUpRequest;
import com.stockcomp.response.AuthenticationResponse;
import com.stockcomp.service.CustomUserService;
import com.stockcomp.util.TokenUtilKt;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserService userService;

    AuthenticationController(AuthenticationManager authenticationManager, CustomUserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> createAuthenticationToken(@RequestBody AuthenticationRequest request) {
        var response = authenticateAndGenerateJwt(request.getUsername(), request.getPassword());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<AuthenticationResponse> signUpUser(@RequestBody SignUpRequest request) {
        var user = userService.addNewUser(request);
        var response = authenticateAndGenerateJwt(request.getUsername(), request.getPassword());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<AuthenticationResponse> signInUser(@RequestBody AuthenticationRequest request) {
        var user = userService.getPersistedUser(request);
        var response = authenticateAndGenerateJwt(request.getUsername(), request.getPassword());

        return ResponseEntity.ok(response);
    }

    private AuthenticationResponse authenticateAndGenerateJwt(String username, String password) {
        var token = new UsernamePasswordAuthenticationToken(username, password);
        authenticationManager.authenticate(token);
        var userDetails = userService.loadUserByUsername(username);

        return new AuthenticationResponse(TokenUtilKt.generateToken(userDetails), username);
    }
}
