package org.example.services;

import org.example.dto.UserDto;
import org.example.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UsersService extends UserDetailsService {
    long save(User user) throws IllegalStateException;
    void delete(long id);
    List<User> listUsers();
    User getUserById(long id);
    void update(long id, User user) throws IllegalStateException;
    User createUserFromDto(UserDto userDto);
}