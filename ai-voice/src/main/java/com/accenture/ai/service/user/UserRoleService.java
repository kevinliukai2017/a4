package com.accenture.ai.service.user;

import java.util.List;

/**
 * Created by lxg
 * on 2017/2/21.
 */
public interface UserRoleService {

    List<String> findRoles(int uid);
}
