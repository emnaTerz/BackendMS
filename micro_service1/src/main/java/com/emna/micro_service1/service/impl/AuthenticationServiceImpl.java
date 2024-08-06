package com.emna.micro_service1.service.impl;

import com.emna.micro_service1.dao.request.SignUpRequest;
import com.emna.micro_service1.dao.request.SigninRequest;
import com.emna.micro_service1.dao.response.JwtAuthenticationResponse;
import com.emna.micro_service1.entities.User;
import com.emna.micro_service1.repository.UserRepository;
import com.emna.micro_service1.service.AuthenticationService;
import com.emna.micro_service1.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

   @Override
   public JwtAuthenticationResponse signup(SignUpRequest request) {
       var user = User.builder()
               .firstName(request.getFirstName())
               .lastName(request.getLastName())
               .email(request.getEmail())
               .password(passwordEncoder.encode(request.getPassword())) // Ensure the password is encoded
               .role(request.getRole())
               .build();
       userRepository.save(user);
       var jwt = jwtService.generateToken(user);
       return JwtAuthenticationResponse.builder().token(jwt).role(user.getRole().name()).build();
   }




    @Override
    public JwtAuthenticationResponse signin(SigninRequest request) {
        try {
            System.out.println("Attempting to authenticate user with email: " + request.getPassword());
            var user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
            System.out.println("User found: " + user);

            boolean passwordsMatch = passwordEncoder.matches(request.getPassword(), user.getPassword());
            System.out.println("Passwords match: " + passwordsMatch);

            if (!passwordsMatch) {
                System.out.println("Attempting to authenticate user with email: " + request.getEmail());
                throw new IllegalArgumentException("Invalid email or password.");
            }

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            System.out.println("Authentication successful for user with email: " + request.getEmail());

            var jwt = jwtService.generateToken(user);
            System.out.println("JWT generated: " + jwt);

            return JwtAuthenticationResponse.builder().token(jwt).role(user.getRole().name()).build();
        } catch (Exception e) {
            System.out.println("Error during authentication: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


}