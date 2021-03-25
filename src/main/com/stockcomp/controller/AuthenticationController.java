package com.stockcomp.controller;

import com.stockcomp.request.AuthenticationRequest;
import com.stockcomp.request.SignUpRequest;
import com.stockcomp.response.AuthenticationResponse;
import com.stockcomp.service.CustomUserService;
import com.stockcomp.util.TokenUtilKt;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthenticationController {

    @Value("cookie.duration")
    private String cookieDuration;

    private final AuthenticationManager authenticationManager;
    private final CustomUserService userService;

    AuthenticationController(AuthenticationManager authenticationManager, CustomUserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping("/authenticate")
    @ApiOperation(value = "Authenticate and create jwt token for user", response = AuthenticationResponse.class)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest request) {
        var response = authenticateAndGenerateJwt(request.getUsername(), request.getPassword());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/sign-up")
    @ApiOperation(value = "Sign up a new user", response = AuthenticationResponse.class)
    public ResponseEntity<?> signUpUser(@RequestBody SignUpRequest request, HttpServletResponse response) {
        userService.addNewUser(request);
        var jwt = authenticateAndGenerateJwt(request.getUsername(), request.getPassword());
        var cookie = createCookie(jwt);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }

    @PostMapping("/sign-in")
    @ApiOperation(value = "Sign in existing user", response = AuthenticationResponse.class)
    public ResponseEntity<?> signInUser(@RequestBody AuthenticationRequest request) {
        userService.getPersistedUser(request);
        var response = authenticateAndGenerateJwt(request.getUsername(), request.getPassword());

        return ResponseEntity.ok(response);
    }

    private String authenticateAndGenerateJwt(String username, String password) {
        var token = new UsernamePasswordAuthenticationToken(username, password);
        authenticationManager.authenticate(token);
        var userDetails = userService.loadUserByUsername(username);

        return TokenUtilKt.generateToken(userDetails);
    }

    private ResponseCookie createCookie(String jwt){
        return ResponseCookie.from("jwt", jwt)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Integer.getInteger(cookieDuration))
                .build();
    }
}
