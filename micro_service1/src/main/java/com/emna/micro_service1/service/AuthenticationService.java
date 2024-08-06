package com.emna.micro_service1.service;


import com.emna.micro_service1.dao.request.SignUpRequest;
import com.emna.micro_service1.dao.request.SigninRequest;
import com.emna.micro_service1.dao.response.JwtAuthenticationResponse;

public interface AuthenticationService {
    JwtAuthenticationResponse signup(SignUpRequest request);

    JwtAuthenticationResponse signin(SigninRequest request);
}
