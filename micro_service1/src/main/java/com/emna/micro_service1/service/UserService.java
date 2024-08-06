package com.emna.micro_service1.service;

import com.emna.micro_service1.entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService {
    UserDetailsService userDetailsService();
    void deleteUser(Integer id);
    User updateUser(Integer id, User user);

    List<User> getAllUsers();
    User getUserById(Integer id);


}