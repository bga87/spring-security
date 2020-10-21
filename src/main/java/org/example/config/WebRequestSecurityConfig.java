package org.example.config;

import org.example.config.handler.LoginSuccessHandler;
import org.example.model.Role;
import org.example.model.SecurityDetails;
import org.example.model.User;
import org.example.services.UsersService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class WebRequestSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UsersService usersService;

    public WebRequestSecurityConfig(UsersService usersService, Set<Role> availableRoles) {
        this.usersService = usersService;
        createInitialAccounts(availableRoles);
    }

    // Создаем первоначальные учетные записи записи
    private void createInitialAccounts(Set<Role> availableRoles) {
        // Админ наделен всеми доступными правами
        usersService.save(
                new User("admin", "admin", (byte) 0, null,
                        new SecurityDetails("admin", "admin", availableRoles)
                )
        );
        // Обычный пользователь для тестирования, чтобы не
        // приходилось каждый раз создавать его из-под админа
        usersService.save(
                new User("user", "user", (byte) 0, null,
                        new SecurityDetails("user", "user", availableRoles.stream()
                                .filter(role -> role.getRoleName().equals("ROLE_USER")).collect(Collectors.toSet())))
        );
    }

    @Bean
    public UserIdMatcher userIdMatcher() {
        return new UserIdMatcher();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(usersService).passwordEncoder(NoOpPasswordEncoder.getInstance());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/users/user/show/{userId}").access(
                        "@userIdMatcher.isAuthenticatedUserId(authentication, #userId) or hasAuthority('ROLE_ADMIN')"
                    )
                .antMatchers("/users/user/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .antMatchers("/users/**").hasAuthority("ROLE_ADMIN")
                .and()
                .formLogin().successHandler(new LoginSuccessHandler())
                .and()
                .exceptionHandling().accessDeniedPage("/users/authorizationFailure")
                .and()
                .csrf().disable();
    }

    private static class UserIdMatcher {
        public boolean isAuthenticatedUserId(Authentication auth, long id) {
            return ((User) auth.getPrincipal()).getId() == id;
        }
    }
}