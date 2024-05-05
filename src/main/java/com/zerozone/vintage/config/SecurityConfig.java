package com.zerozone.vintage.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())) // CSRF 보호 활성화
                .authorizeHttpRequests((authorizeRequests) ->
                     authorizeRequests
                         .requestMatchers("/register","/api/account/register",
                           "/email-verification", "/api/account/email-verification", "/checked-email"
                         ).permitAll()
                         .requestMatchers("/").permitAll()
                         .requestMatchers("/favicon.ico").permitAll()
                         .requestMatchers(// Swagger 허용 URL
                                         "/v2/api-docs", "/v3/api-docs", "/v3/api-docs/**", "/swagger-resources",
                                         "/swagger-resources/**", "/configuration/ui", "/configuration/security", "/swagger-ui/**",
                                         "/webjars/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated() // 그외는 로그인 해야만 접근 가능
                )
                .formLogin((formLogin) ->{
                    formLogin.loginPage("/login").permitAll();
                })
                .logout(log -> log.logoutSuccessUrl("/"))

                .build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/node_modules/**")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public SecurityContextHolderStrategy securityContextHolderStrategy() {
        return SecurityContextHolder.getContextHolderStrategy();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }


}
