package com.company.training.service;

import com.company.training.domain.User;

public interface UserRepository {

    User findByEmail(String email);

    User save(User user);
}
