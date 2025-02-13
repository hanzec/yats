package com.hanzec.yats.config;

import com.google.gson.Gson;
import com.hanzec.yats.service.AccountService;
import com.hanzec.yats.service.security.PasswordAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.session.HttpSessionEventPublisher;

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


//
//    public SpringSecurityConfig(Gson gson,
//                                AccountService accountService,
//                                PasswordEncoder passwordEncoder,
//                                PasswordAuthenticationProvider passwordAuthenticationProvider, PasswordAuthenticationProvider passwordAuthenticationProvider1) {
//        this.gson = gson;
//        this.accountService = accountService;
//        this.passwordEncoder = passwordEncoder;
//        this.passwordAuthenticationProvider = passwordAuthenticationProvider;
//    }
//


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authz) -> authz
                        .anyRequest().authenticated()
                )
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
                        .userDetailsService(accountService)
                        .rememberMeCookieName("remember-me-cookie"));


        // authorize requests
        http
                .authorizeHttpRequests(authorized -> authorized
                        .requestMatchers("/login").permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }
//
//    @Override
//    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
//        //Provider for session Login
//        authenticationManagerBuilder
//                ;
//    }
//
//    @Override
//    @Bean(BeanIds.AUTHENTICATION_MANAGER)
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return super.authenticationManagerBean();
//    }
//
//    @Bean
//    public HttpSessionEventPublisher httpSessoinEventPublisher() {
//        return new HttpSessionEventPublisher();
//    }
//
//    @Override
//    public void configure(WebSecurity web) {
//        //ignoring static objects
//        web.ignoring()
//                .antMatchers("/error")
//                .antMatchers("/index.html")
//                .antMatchers("/swagger-ui/**")
//                .antMatchers("/v3/api-docs/**")
//                .antMatchers("/swagger-ui.html")
//                .antMatchers("/client_sign_root.crt");
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {

//    }
}