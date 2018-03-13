package com.accenture.ai.service.user.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.accenture.ai.dao.UserRepository;
import com.accenture.ai.model.User;
import com.accenture.ai.service.user.UserService;

/**
 * Created by lxg
 * on 2017/2/21.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User findByUsername(String name) {
        return userRepository.findByUsername(name);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

}
