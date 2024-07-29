package com.example.spring;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository {
    List<User> getAllUsers();
    User getById(Long id);
    void createUser(User user);
    void updateUser(Long id , User user);
    void deleteUser(Long id);
}
