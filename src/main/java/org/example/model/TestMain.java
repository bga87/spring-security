package org.example.model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;


public class TestMain {

    public static void main(String[] args) {
        SessionFactory sessionFactory =
                new MetadataSources(
                        new StandardServiceRegistryBuilder()
                                .applySetting("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver")
                                .applySetting("hibernate.connection.url", "jdbc:mysql://localhost/jm_users_crud")
                                .applySetting("hibernate.connection.username", "root")
                                .applySetting("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect")
                                .applySetting("hibernate.show_sql", "true")
                                .applySetting("hibernate.hbm2ddl.auto", "create").build()
                ).addAnnotatedClass(User.class).addAnnotatedClass(Job.class)
                        .buildMetadata().buildSessionFactory();

        Transaction tx = null;

        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            User u = new User("Bob", "Berg", (byte) 54, null);
            session.save(u);
            System.out.println(u.getId());
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
