package org.example.services;

import org.example.dao.UserDao;
import org.example.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsersServiceImpl implements UsersService {

    private final UserDao userDao;

    @Autowired
    public UsersServiceImpl(UserDao userDao) {
        this.userDao = userDao;
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
    public void update(long id, User user) throws IllegalStateException {
        userDao.update(id, user);
    }
}