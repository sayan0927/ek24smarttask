package com.example.smart_task_management.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {


    @Autowired
    PasswordEncoder passwordEncoder;


    @Autowired
    UserDetailsService userDetailsService;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests
                        (
                                authorize -> authorize


                                        .requestMatchers("/auth/**", "/dist/**").permitAll()
                                        .requestMatchers("/css/**", "/js/**").permitAll()
                                        .requestMatchers("/auth/**", "/dist/**").permitAll()

                                        //permitted endpoints
                                        .requestMatchers("/").hasAnyAuthority("ADMIN", "USER")
                                        .requestMatchers("/login","/users/register/page","users/register").permitAll()

                                        //admin endpoints
                                        .requestMatchers("/admin/**").hasAnyAuthority("ADMIN")

                                        //common endpoints
                                        .requestMatchers("/tasks/**").hasAnyAuthority("ADMIN","USER")
                                        .requestMatchers("/users").permitAll()
                                        .requestMatchers("/users/**").hasAnyAuthority("ADMIN","USER")





                                        .anyRequest().permitAll()


                        )

                .formLogin(form -> form.permitAll().loginPage("/login").loginProcessingUrl("/login"))
                .csrf(csrf -> csrf.disable());
        return http.build();


    }


}
