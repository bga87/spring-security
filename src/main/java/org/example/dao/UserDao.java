package org.example.dao;

import org.example.model.User;

import java.util.List;

public interface UserDao {
    long save(User user);
    void delete(long id);
    List<User> listUsers();
    User getUserById(long id);
    void update(User user);
}
