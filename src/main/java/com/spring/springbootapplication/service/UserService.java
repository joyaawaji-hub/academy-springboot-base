package com.spring.springbootapplication.service;

import com.spring.springbootapplication.form.RegisterForm;
import com.spring.springbootapplication.mapper.UserMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public boolean existsByEmail(String email) {
        return userMapper.countByEmail(email) > 0;
    }

    public void register(RegisterForm form) {

        String hashedPassword =
            passwordEncoder.encode(form.getPassword());

        userMapper.insert(
            form.getEmail(),
            hashedPassword,
            form.getUserName()
        );
    }
}
