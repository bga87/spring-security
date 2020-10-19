package org.example.config;

import org.example.model.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

import javax.servlet.ServletContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Configuration
@ComponentScan(basePackages = {"org.example.dao", "org.example.services"})
@Import({PersistenceConfig.class, WebRequestSecurityConfig.class})
public class RootConfig {

    @Bean("availableRoles")
    public Set<Role> availableRoles(ServletContext context) {
        System.out.println("Creating avRoles bean");
        Set<Role> roles = new HashSet<>(
                Arrays.asList(
                        new Role("ROLE_USER", "Пользователь"),
                        new Role("ROLE_ADMIN", "Администратор")
                )
        );
        System.out.println("Bean created " + roles);
        context.setAttribute("availableRoles", roles);
        return roles;
    }
}