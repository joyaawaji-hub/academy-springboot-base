package com.spring.springbootapplication.service;

import com.spring.springbootapplication.form.RegisterForm;
import com.spring.springbootapplication.mapper.UserMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

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

    public Map<String, Object> authenticate(
            String email,
            String rawPassword) {

        Map<String, Object> user =
            userMapper.findByEmail(email);

        if (user == null) {
            return null;
        }

        String hashedPassword =
            (String) user.get("password");

        if (!passwordEncoder.matches(
                rawPassword,
                hashedPassword)) {
            return null;
        }

        return user;
    }
}