package org.example.config;

import org.example.model.Role;
import org.example.model.SecurityDetails;
import org.example.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Configuration
@ComponentScan(basePackages = {"org.example.dao", "org.example.dto", "org.example.services"})
@Import({PersistenceConfig.class, WebRequestSecurityConfig.class})
public class RootConfig {

    @Bean("availableRoles")
    public Set<Role> userRole() {
        return new HashSet<>(Arrays.asList(new Role("ROLE_USER"), new Role("ROLE_ADMIN")));
    }

    @Bean("adminUser")
    @Transactional
    public User adminUser(EntityManagerFactory entityManagerFactory, Set<Role> availableRoles) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.joinTransaction();

        User user = new User("admin", "admin", (byte) 0, null);
        SecurityDetails securityDetails = new SecurityDetails("admin", "admin", availableRoles);
        user.setSecurityDetails(securityDetails);
        em.persist(user);

        return user;
    }
}