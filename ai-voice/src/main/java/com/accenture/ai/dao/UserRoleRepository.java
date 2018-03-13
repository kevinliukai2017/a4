package com.accenture.ai.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.accenture.ai.model.UserRole;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole,Long> {

    List<UserRole> findByuid(int uid);

    @Query(value = "select r.role_name from tb_user_role ur left join tb_role r on ur.rid=r.id where ur.uid = ?1",nativeQuery = true)
    List<String> findRoleName(int uid);
}
