package com.accenture.ai.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.accenture.ai.model.User;

/**
 * Created by lxg
 * on 2017/2/20.
 */
@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    User findByUsername(String name);

}
