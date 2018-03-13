package com.accenture.ai.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.accenture.ai.model.Role;

/**
 * Created by lxg
 * on 2017/2/20.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
}
