package org.example.services;

import org.example.model.User;

import java.util.List;

public interface UsersService {
    long save(User user);
    void delete(long id);
    List<User> listUsers();
    User getUserById(long id);
    void update(User user);
}
