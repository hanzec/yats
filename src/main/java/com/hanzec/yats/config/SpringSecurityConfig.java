package com.hanzec.yats.config;

import com.hanzec.yats.service.AccountService;
import com.hanzec.yats.service.security.provider.PasswordAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {
    private final AccountService accountService;


    private final PasswordAuthenticationProvider passwordAuthenticationProvider;


    public SpringSecurityConfig(AccountService accountService, PasswordAuthenticationProvider passwordAuthenticationProvider) {
        this.accountService = accountService;
        this.passwordAuthenticationProvider = passwordAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            PasswordEncoder passwordEncoder) {
        UsernamePasswordAuthenticationFilter filter;
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(accountService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authenticationProvider, passwordAuthenticationProvider);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(withDefaults())
                .authenticationManager(new ProviderManager(
                        passwordAuthenticationProvider
                ));

        // login page configuration
        http
                .formLogin(formLogin -> {
                            formLogin
                                    .loginPage("/login")
                                    .loginProcessingUrl("/api/v1/login")
                                    .usernameParameter("email")
                                    .passwordParameter("password")
                                    .permitAll();
                        }
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .deleteCookies("JSESSIONID")
                )

                .sessionManagement(sessionManagement -> sessionManagement
                        .invalidSessionUrl("/session/invalid")
                )

                .rememberMe(rememberMe -> rememberMe.key("remember-me-key")
                        .rememberMeParameter("remember-me")
                        .tokenValiditySeconds(60 * 60 * 24 * 7)
                        .rememberMeCookieName("remember-me-cookie"));
        http
                .oauth2Login(withDefaults());

        // authorize requests
        http
                .authorizeHttpRequests(authorized -> authorized
                        .requestMatchers("/login").permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}