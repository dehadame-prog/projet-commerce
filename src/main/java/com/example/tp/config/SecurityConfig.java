package com.example.tp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setHideUserNotFoundExceptions(false);
        return authProvider;
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            String errorUrl = "/login?error=true";
            if (exception instanceof UsernameNotFoundException) {
                errorUrl += "&reason=notfound";
            } else if (exception instanceof DisabledException) {
                errorUrl += "&reason=disabled";
            } else {
                errorUrl += "&reason=badcredentials";
            }
            response.sendRedirect(errorUrl);
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider authenticationProvider)
            throws Exception {
        http
                .authenticationProvider(authenticationProvider)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.GET, "/", "/products", "/products/**", "/articles", "/articles/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/cart", "/cart/**", "/payments", "/payments/**")
                        .authenticated()
                        .requestMatchers(HttpMethod.POST, "/cart/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/products").hasAnyRole("ADMIN", "MERCHANT")
                        .requestMatchers(HttpMethod.GET, "/products/*/edit").hasAnyRole("ADMIN", "MERCHANT")
                        .requestMatchers(HttpMethod.POST, "/products/*/update").hasAnyRole("ADMIN", "MERCHANT")
                        .requestMatchers(HttpMethod.POST, "/products/*/delete").hasAnyRole("ADMIN", "MERCHANT")
                        .requestMatchers(HttpMethod.GET, "/api/products", "/api/products/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/products").hasAnyRole("ADMIN", "MERCHANT")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAnyRole("ADMIN", "MERCHANT")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAnyRole("ADMIN", "MERCHANT")
                        .requestMatchers(HttpMethod.POST, "/articles").hasAnyRole("ADMIN", "MERCHANT")
                        .requestMatchers(HttpMethod.POST, "/articles/*/delete").hasAnyRole("ADMIN", "MERCHANT")
                        .requestMatchers(HttpMethod.POST, "/articles/*/comments").authenticated()
                        .requestMatchers(HttpMethod.POST, "/articles/*/comments/*/update").authenticated()
                        .requestMatchers(HttpMethod.POST, "/articles/*/comments/*/delete").authenticated()
                        .requestMatchers(HttpMethod.GET, "/admin/**").hasRole("ADMIN")
                        .requestMatchers("/register", "/login", "/h2-console/**", "/css/**", "/js/**", "/uploads/**").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .failureHandler(authenticationFailureHandler())
                        .defaultSuccessUrl("/", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                        .logoutSuccessUrl("/")
                        .permitAll())
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .headers(headers -> headers.frameOptions().sameOrigin());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
