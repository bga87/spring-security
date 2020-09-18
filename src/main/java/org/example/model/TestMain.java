package org.example.model;

import org.example.dao.UserDao;
import org.example.dao.UserDaoImpl;


public class TestMain {

    private static final UserDao USER_DAO = new UserDaoImpl();

    public static void main(String[] args) {
        saveToDatabase(new User("Bob", "Berg", (byte) 54, null));
        System.out.println();
        saveToDatabase(new User("John", "Black", (byte) 32, new Job("Driver", 43000)));
        System.out.println();
        saveToDatabase(new User("Mike", "Phillips", (byte) 32, new Job("Driver", 43000)));
        System.out.println();
        saveToDatabase(new User("Mike", "Phillips", (byte) 32, new Job("Accountant", 75000)));
        System.out.println();
        saveToDatabase(new User("Mike", "Phillips", (byte) 32, new Job("Accountant", 75000)));
        System.out.println();
        saveToDatabase(new User("Mike", "Phillips", (byte) 32, null));
        System.out.println();
        saveToDatabase(new User("Mike", "Phillips", (byte) 32, null));
    }

    private static void saveToDatabase(User user) {
        USER_DAO.save(user);
    }
}
