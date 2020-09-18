package org.example.dao;

import org.example.model.Job;
import org.example.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.List;

public class UserDaoImpl implements UserDao {

    private static final SessionFactory SESSION_FACTORY =
            new MetadataSources(
                    new StandardServiceRegistryBuilder()
                            .applySetting("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver")
                            .applySetting("hibernate.connection.url", "jdbc:mysql://localhost/jm_users_crud")
                            .applySetting("hibernate.connection.username", "root")
                            .applySetting("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect")
                            .applySetting("hibernate.show_sql", "true")
                            .applySetting("hibernate.hbm2ddl.auto", "create").build()
            ).addAnnotatedClass(User.class).addAnnotatedClass(Job.class).addPackage("org.example.model")
                    .buildMetadata().buildSessionFactory();


    @Override
    public void save(User user) {
        Transaction tx = null;

        try (Session session = SESSION_FACTORY.openSession()) {
            tx = session.beginTransaction();

            user.getJob().flatMap(
                    userJob -> session.createQuery("FROM Job j " +
                            "WHERE j.name = :name AND j.salary = :salary", Job.class)
                            .setParameter("name", userJob.getName())
                            .setParameter("salary", userJob.getSalary())
                            .uniqueResultOptional()
            ).ifPresent(user::setJob);

            List<User> possiblyIdenticalUsers = session.createQuery("FROM User u " +
                    "WHERE u.name = :name AND u.surname = :surname AND u.age = : age", User.class)
                    .setParameter("name", user.getName())
                    .setParameter("surname", user.getSurname())
                    .setParameter("age", user.getAge())
                    .list();

            if (!possiblyIdenticalUsers.contains(user)) {
                session.save(user);
            } else {
                System.out.println("Duplicate user found: " + user);
            }

            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                System.out.println("Rolling back");
                tx.rollback();
            }
            ex.printStackTrace();
        }
    }
}
