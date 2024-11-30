package com.doublehexa.game.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Desabilita CSRF temporariamente para desenvolvimento
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/home", "/register", "/login", "/css/**", "/js/**", "/webjars/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()  // Permite acesso ao console H2
                        .requestMatchers("/ws/**").permitAll()  // Permite acesso ao WebSocket
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions().disable())  // Para o console H2 funcionar
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/game/lobby", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Apenas para teste durante desenvolvimento
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder().encode("password"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user);
    }
}