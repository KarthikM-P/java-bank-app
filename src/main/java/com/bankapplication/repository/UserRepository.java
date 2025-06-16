package com.bankapplication.repository;

import com.bankapplication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  boolean existsByEmail(String email);
  boolean existsByAccountNumber(String accountNumber);
  User findByAccountNumber(String accountNumber);

}

