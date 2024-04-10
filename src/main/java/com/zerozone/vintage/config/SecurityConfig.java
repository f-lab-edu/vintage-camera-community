package com.zerozone.vintage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                .authorizeHttpRequests((authorizeRequests) ->
                     authorizeRequests
                         .requestMatchers("/signUp").permitAll()
                         .requestMatchers("/login").permitAll()
                         .requestMatchers("/").permitAll()
                         .requestMatchers("/css/**").permitAll()
                         .requestMatchers("/js/**").permitAll()
                         .requestMatchers("/img/**").permitAll()
                         .requestMatchers("/favicon.ico").permitAll()
                        .anyRequest().authenticated() // 로그인 해야만 접근 가능
                ).build();
    }



}
