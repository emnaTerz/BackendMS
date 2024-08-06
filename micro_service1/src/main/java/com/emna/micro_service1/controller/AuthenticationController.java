package com.emna.micro_service1.controller;

import com.emna.micro_service1.dao.request.SignUpRequest;
import com.emna.micro_service1.dao.request.SigninRequest;
import com.emna.micro_service1.dao.response.JwtAuthenticationResponse;
import com.emna.micro_service1.entities.User;
import com.emna.micro_service1.service.AuthenticationService;
import com.emna.micro_service1.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/*@CrossOrigin(origins = "http://localhost:4200")*/
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserService userService;
    @PostMapping("/signup")
    public ResponseEntity<JwtAuthenticationResponse> signup(@RequestBody SignUpRequest request) {
        return ResponseEntity.ok(authenticationService.signup(request));
    }


   @PostMapping("/signin")
   public ResponseEntity<JwtAuthenticationResponse> signin(@RequestBody SigninRequest request) {
       System.out.println("Attempting to authenticate user with email: " + request.getEmail());
       System.out.println("Attempting to authenticate user with email: " + request.getPassword());
       try {
           JwtAuthenticationResponse response = authenticationService.signin(request);
           System.out.println("Authentication successful, token generated: " + response.getToken());
           return ResponseEntity.ok(response);
       } catch (Exception e) {
           System.out.println("Authentication failed: " + e.getMessage());
           return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
       }
   }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User user) {
        System.out.println("user" +user);
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
