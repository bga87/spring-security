package org.example.dao;

import org.example.model.Job;
import org.example.model.Role;
import org.example.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Repository("userDao")
public class UserDaoImpl implements UserDao {

    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public UserDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public long save(User user) throws IllegalStateException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.joinTransaction();

        setJobFromPersistentIfAlreadyExists(user, entityManager);
        setRolesFromPersistentIfAlreadyExists(user, entityManager);

        long savedUserId = -1;
        if (userHasUniqueLogin(user, entityManager)) {
            if (!userIsAlreadyPersisted(user, entityManager)) {
                entityManager.persist(user);
                savedUserId = user.getId();
            } else {
                throw new IllegalStateException("Пользователь " + user + " был сохранен в базе данных ранее");
            }
        } else {
            throw new IllegalStateException("Логин " + user.getSecurityDetails().getLogin() + " уже занят! Придумайте другой логин.");
        }

        return savedUserId;
    }

    private void setRolesFromPersistentIfAlreadyExists(User user, EntityManager entityManager) {
        Set<Role> correspondingPersistentRoles = user.getSecurityDetails().getRoles().stream()
                .filter(role -> role.getId() != null)
                .map(entityManager::merge)
                .collect(Collectors.toSet());

        if (correspondingPersistentRoles.size() > 0) {
            user.getSecurityDetails().setRoles(correspondingPersistentRoles);
        }
    }

    private boolean userIsAlreadyPersisted(User user, EntityManager entityManager) {
        return entityManager.createQuery("SELECT u FROM User u " +
                "WHERE u.name = :name AND u.surname = :surname AND u.age = :age", User.class)
                .setParameter("name", user.getName())
                .setParameter("surname", user.getSurname())
                .setParameter("age", user.getAge())
                .getResultList()
                .contains(user);
    }

    private boolean userHasUniqueLogin(User user, EntityManager entityManager) {
        return entityManager.createQuery("SELECT u FROM User u " +
                "WHERE u.securityDetails.login = :login", User.class)
                .setParameter("login", user.getSecurityDetails().getLogin())
                .getResultList().size() == 0;
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
    public void delete(long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.joinTransaction();

        User loadedUser = entityManager.find(User.class, id);

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
            // на эту запись job больше никто не ссылается из таблицы user, ее можно удалять
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
    public User getUserById(long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.joinTransaction();
        return entityManager.find(User.class, id);
    }

    @Override
    public User getUserByLogin(String login) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.joinTransaction();
        return entityManager.createQuery("SELECT u FROM User u " +
                "WHERE u.securityDetails.login = :login", User.class)
                .setParameter("login", login)
                .getSingleResult();
    }

    @Override
    public void update(long id, User user) throws IllegalStateException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.joinTransaction();

        User targetUser = entityManager.find(User.class, id);

        // Если изменненый user идентичен соответствующему ему из БД,
        // то дальнейшее выполнение метода не имеет смысла
        if (targetUser.equals(user)) {
            return;
        }

        if (!userIsAlreadyPersisted(user, entityManager)) {
            targetUser.setName(user.getName());
            targetUser.setSurname(user.getSurname());
            targetUser.setAge(user.getAge());

            if (!targetUser.getSecurityDetails().equals(user.getSecurityDetails())) {
                // данные доступа поменялись
                if (!loginsAreTheSame(targetUser.getSecurityDetails().getLogin(), user.getSecurityDetails().getLogin()) &&
                        !userHasUniqueLogin(user, entityManager)) {
                    throw new IllegalStateException("Логин " + user.getSecurityDetails().getLogin() + " уже занят! Придумайте другой логин.");
                }
                targetUser.setSecurityDetails(user.getSecurityDetails());
                setRolesFromPersistentIfAlreadyExists(targetUser, entityManager);
            }

            if (!jobsAreTheSame(targetUser.getJob(), user.getJob())) {
                setJobFromPersistentIfAlreadyExists(user, entityManager);
                Optional<Job> oldUserJob = targetUser.getJob();
                targetUser.setJob(user.getJob().orElse(null));
                oldUserJob.ifPresent(oldJob -> removeIfOrphanJob(oldJob, entityManager));
            }
        } else {
            throw new IllegalStateException("Пользователь " + user + " был сохранен в базе данных ранее");
        }
    }

    private boolean loginsAreTheSame(String originalLogin, String newLogin) {
        return originalLogin.equals(newLogin);
    }

    private boolean jobsAreTheSame(Optional<Job> originalJobOpt, Optional<Job> newJobOpt) {
        return Objects.equals(originalJobOpt.orElse(null), newJobOpt.orElse(null));
    }
}