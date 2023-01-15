package com.develop.assignmentPart2.repository;

import com.develop.assignmentPart2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    public User findByEmail(String email);
    public boolean existsByUsername(String username);
    public boolean existsByEmail(String Email);
}
