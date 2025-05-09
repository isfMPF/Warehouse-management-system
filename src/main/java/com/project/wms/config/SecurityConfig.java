package com.project.wms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers( "/css/**", "/js/**").permitAll()
                        .requestMatchers("/logistics").hasRole("ADMIN")
                        .requestMatchers("/promotions/create").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/clientDelete/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/productDelete/**").hasRole("ADMIN")
                        .requestMatchers("/logistics/export").hasRole("ADMIN")
                        .anyRequest().authenticated()

                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/main", true) // Всегда перенаправлять на /main после входа
                        .permitAll()
                )
                .logout(logout -> logout
                        .permitAll()

                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/logistics/export")
                );
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("user")
                .roles("USER")
                .build();


        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("Admin")
                .password("admin")
                .roles("USER", "ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }
}