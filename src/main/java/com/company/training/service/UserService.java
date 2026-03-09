package com.company.training.service;

import com.company.training.domain.User;

public class UserService {

    private final UserRepository userRepository;
    private final EmailSender emailSender;

    public UserService(UserRepository userRepository, EmailSender emailSender) {
        this.userRepository = userRepository;
        this.emailSender = emailSender;
    }

    public User register(String email, String name) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email must not be empty");
        }
        if (userRepository.findByEmail(email) != null) {
            throw new IllegalStateException("User already exists");
        }

        User created = userRepository.save(new User(null, email, name));
        emailSender.sendWelcomeEmail(created.getEmail());
        return created;
    }
}
