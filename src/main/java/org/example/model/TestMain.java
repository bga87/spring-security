package org.example.model;

import org.example.config.PersistenceConfig;
import org.example.services.UsersService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TestMain {

    private static final AnnotationConfigApplicationContext context =
            new AnnotationConfigApplicationContext(PersistenceConfig.class);

    public static void main(String[] args) {

        UsersService usersService = context.getBean(UsersService.class);


        usersService.save(new User("Bob", "Berg", (byte) 54, null));
        usersService.save(new User("John", "Black", (byte) 32, new Job("Driver", 43000)));
        usersService.save(new User("Mike", "Phillips", (byte) 32, new Job("Driver", 43000)));

        User mikeAccount = new User("Mike", "Phillips", (byte) 32, new Job("Accountant", 75000));
        usersService.save(mikeAccount);

        usersService.save(new User("Mike", "Phillips", (byte) 32, new Job("Accountant", 75000)));
        usersService.save(new User("Mike", "Phillips", (byte) 32, null));
        usersService.save(new User("Mike", "Phillips", (byte) 32, null));

        User testUser = new User("Bill", "Murray", (byte) 23, new Job("Seller", 23000));
        User testUser2 = new User("Tommy", "Angelo", (byte) 36, new Job("Seller", 23000));
        usersService.save(testUser);
        usersService.save(testUser2);

        printAllUsers(usersService);

        System.out.println("Deleting " + testUser);
        usersService.delete(testUser);

        printAllUsers(usersService);

        System.out.println("Updating " + testUser2);
        testUser2.setName("Henry");
        testUser2.setJob(new Job("Lawer", 175000));
        System.out.println("To " + testUser2);

        usersService.update(testUser2);

        printAllUsers(usersService);

        System.out.println("Updating " + mikeAccount);
        mikeAccount.setName("Gary");
        mikeAccount.setJob(new Job("Lawer", 175000));
        System.out.println("To " + mikeAccount);

        usersService.update(mikeAccount);

        printAllUsers(usersService);
    }

    private static void printAllUsers(UsersService usersService) {
        System.out.println();
        for (User u : usersService.listUsers()) {
            System.out.println(u);
        }
        System.out.println();
    }
}


