package com.accenture.ai.service.user.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.accenture.ai.dao.UserRoleRepository;
import com.accenture.ai.service.user.UserRoleService;

/**
 * Created by lxg
 * on 2017/2/21.
 */
@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private UserRoleRepository userRoleRepository;
    @Override
    public List<String> findRoles(int uid) {

        List<String> list = userRoleRepository.findRoleName(uid);

        return list;
    }
}
