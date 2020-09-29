package org.example.services;

import org.example.model.User;

import java.util.List;

public interface UsersService {
    long save(User user);
    void delete(User user);
    List<User> listUsers();
    void update(User user);
}
