package com.example.smart_task_management.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
public class WebSecurityConfig {


    @Autowired
    PasswordEncoder passwordEncoder;


    @Autowired
    UserDetailsService userDetailsService;

    @Bean
    AuthenticationEntryPoint authenticationEntryPoint()
    {
        return http401And403Handler();
    }

    @Bean
    AccessDeniedHandler accessDeniedHandler()
    {
        return http401And403Handler();
    }
    @Bean
    public Http401And403Handler http401And403Handler() {return new Http401And403Handler();}


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests
                        (
                                authorize -> authorize


                                        .requestMatchers("/auth/**", "/dist/**").permitAll()
                                        .requestMatchers("/css/**", "/js/**").permitAll()
                                        .requestMatchers("/auth/**", "/dist/**").permitAll()

                                        //permitted endpoints
                                        .requestMatchers("/login","/users/register/page","users/register","/401","/403").permitAll()
                                        .requestMatchers("/users").permitAll() // for user register



                                        //admin endpoints
                                        .requestMatchers("/admin/**").hasAnyAuthority("ADMIN")

                                        //common endpoints
                                        .requestMatchers("/").hasAnyAuthority("ADMIN", "USER")
                                        .requestMatchers("/tasks/**").hasAnyAuthority("ADMIN","USER")

                                        .requestMatchers("/users/**").hasAnyAuthority("ADMIN","USER")





                                        .anyRequest().permitAll()


                        )

                .formLogin(form -> form.permitAll().loginPage("/login").loginProcessingUrl("/login"))
                .csrf(csrf -> csrf.disable())
        .exceptionHandling(ex-> ex.authenticationEntryPoint(authenticationEntryPoint()).accessDeniedHandler(accessDeniedHandler()));
        return http.build();


    }


}
