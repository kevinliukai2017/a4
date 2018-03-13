package com.accenture.ai.service.user;

import java.util.List;

import com.accenture.ai.model.User;

/**
 * Created by lxg
 * on 2017/2/21.
 */
public interface UserService {

    User findByUsername(String name);

    List<User> findAll();

}
