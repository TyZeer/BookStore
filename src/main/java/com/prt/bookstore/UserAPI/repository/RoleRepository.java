package com.prt.bookstore.UserAPI.repository;


import com.project.UserAPI.models.ERole;
import com.project.UserAPI.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(ERole name);

  
}
