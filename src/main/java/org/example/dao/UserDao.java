package org.example.dao;

import org.example.model.User;

import java.util.List;

public interface UserDao {
    long save(User user);
    void delete(User user);
    List<User> listUsers();
    void update(User user);
}
