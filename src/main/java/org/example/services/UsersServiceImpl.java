package org.example.services;

import org.example.dao.UserDao;
import org.example.dto.UserDto;
import org.example.model.Job;
import org.example.model.Role;
import org.example.model.SecurityDetails;
import org.example.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsersServiceImpl implements UsersService {

    private final UserDao userDao;
    private final Set<Role> availableRoles;

    @Autowired
    public UsersServiceImpl(UserDao userDao, Set<Role> availableRoles) {
        this.userDao = userDao;
        this.availableRoles = availableRoles;
    }

    @Transactional
    @Override
    public long save(User u) throws IllegalStateException {
        return userDao.save(u);
    }

    @Transactional
    @Override
    public void delete(long id) {
        userDao.delete(id);
    }

    @Transactional
    @Override
    public List<User> listUsers() {
        return userDao.listUsers();
    }

    @Transactional
    @Override
    public User getUserById(long id) {
        return userDao.getUserById(id);
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userDao.getUserByLogin(s);
    }

    @Transactional
    @Override
    public void update(long id, User user) throws IllegalStateException {
        userDao.update(id, user);
    }

    @Override
    public User createUserFromDto(UserDto userDto) {
        Job job = !userDto.getJobName().isEmpty() ? new Job(userDto.getJobName(), userDto.getSalary()) : null;
        Set<Role> userRoles = availableRoles.stream()
                .filter(role -> userDto.getRoles().contains(role.getRoleName()))
                .collect(Collectors.toSet());
        return new User(userDto.getName(), userDto.getSurname(), userDto.getAge(), job,
                new SecurityDetails(userDto.getLogin(), userDto.getPassword(), userRoles));

    }
}