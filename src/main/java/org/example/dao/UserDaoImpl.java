package org.example.dao;

import org.example.model.Job;
import org.example.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository("userDao")
public class UserDaoImpl implements UserDao {

    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public UserDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public long save(User user) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.joinTransaction();

        setJobFromPersistentIfAlreadyExists(user, entityManager);

        List<User> possiblyIdenticalUsers = entityManager.createQuery("SELECT u FROM User u " +
                "WHERE u.name = :name AND u.surname = :surname AND u.age = : age", User.class)
                .setParameter("name", user.getName())
                .setParameter("surname", user.getSurname())
                .setParameter("age", user.getAge())
                .getResultList();

        long savedUserId = -1;
        if (!possiblyIdenticalUsers.contains(user)) {
            entityManager.persist(user);
            savedUserId = user.getId();
        } else {
            System.out.println("Duplicate user found: " + user);
        }

        return savedUserId;
    }

    private void setJobFromPersistentIfAlreadyExists(User user, EntityManager entityManager) {
        user.getJob().flatMap(userJob ->
                entityManager.createQuery("SELECT j FROM Job j " +
                        "WHERE j.name = :name AND j.salary = :salary", Job.class)
                        .setParameter("name", userJob.getName())
                        .setParameter("salary", userJob.getSalary())
                        .getResultStream().findFirst()
        ).ifPresent(user::setJob);
    }

    @Override
    public void delete(User user) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.joinTransaction();

        User loadedUser = entityManager.find(User.class, user.getId());

        if (loadedUser != null) {
            entityManager.remove(loadedUser);
            loadedUser.getJob().ifPresent(job -> removeIfOrphanJob(job, entityManager));
        }
    }

    private void removeIfOrphanJob(Job job, EntityManager entityManager) {
        List<User> usersWithTheSameJob = entityManager.createQuery("SELECT u FROM User u " +
                "WHERE u.job = :job", User.class)
                .setParameter("job", job)
                .getResultList();

        if (usersWithTheSameJob.size() == 0) {
            // на эту запись job больше никто не ссылается из таблицы user,
            // ее можно удалять
            System.out.println("Orphaned entry found");
            entityManager.remove(job);
        }
    }

    @Override
    public List<User> listUsers() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.joinTransaction();
        return entityManager.createQuery("SELECT u FROM User u LEFT JOIN FETCH u.job", User.class).getResultList();
    }

    @Override
    public void update(User user) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.joinTransaction();

        User targetUser = entityManager.find(User.class, user.getId());
        Optional<Job> oldUserJob = Optional.empty();

        if (!jobsAreTheSame(targetUser.getJob(), user.getJob())) {
            user.getJob().ifPresent(job -> setJobFromPersistentIfAlreadyExists(user, entityManager));
            oldUserJob = targetUser.getJob();
        }

        entityManager.merge(user);
        oldUserJob.ifPresent(oldJob -> removeIfOrphanJob(oldJob, entityManager));
    }

    private boolean jobsAreTheSame(Optional<Job> originalJobOpt, Optional<Job> newJobOpt) {
        return Objects.equals(originalJobOpt.orElse(null), newJobOpt.orElse(null));
    }
}
