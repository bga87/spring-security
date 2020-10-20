package org.example.config;

import org.example.config.handler.LoginSuccessHandler;
import org.example.model.Role;
import org.example.model.SecurityDetails;
import org.example.model.User;
import org.example.services.UsersService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

import java.util.Set;

@Configuration
@EnableWebSecurity
public class WebRequestSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UsersService usersService;

    public WebRequestSecurityConfig(UsersService usersService, Set<Role> availableRoles) {
        this.usersService = usersService;
        createAdminAccount(availableRoles);
    }

    // Создаем первоначальную запись админа, который наделен всеми доступными правами,
    // чтобы можно было авторизоваться при старте приложения
    private void createAdminAccount(Set<Role> availableRoles) {
        usersService.save(
                new User("admin", "admin", (byte) 0, null,
                        new SecurityDetails("admin", "admin", availableRoles)
                )
        );
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(usersService).passwordEncoder(NoOpPasswordEncoder.getInstance());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/users**").hasAuthority("ROLE_ADMIN")
                .mvcMatchers("@{/users?action=show*}").hasAuthority("ROLE_USER")

                .and()
                .formLogin().successHandler(new LoginSuccessHandler())
                .and()
                .logout()
                .and()
                .csrf().disable();
    }
}