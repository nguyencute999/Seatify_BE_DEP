package com.seatify.repository;

import com.seatify.model.RoleEntity;
import com.seatify.model.constants.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author : Lê Văn Nguyễn - CE181235
 */
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByRoleName(Role roleName);
}


