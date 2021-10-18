package com.stockcomp.configuration;

import com.stockcomp.controller.common.ExceptionHandlerFilter;
import com.stockcomp.controller.common.TokenAuthenticationFilter;
import com.stockcomp.domain.user.Role;
import com.stockcomp.request.SignUpRequest;
import com.stockcomp.service.user.DefaultUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.PostConstruct;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final DefaultUserService userService;
    private final TokenAuthenticationFilter tokenAuthenticationFilter;
    private final ExceptionHandlerFilter exceptionHandlerFilter;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.email}")
    private String adminEmail;

    public SecurityConfiguration(PasswordEncoder passwordEncoder, TokenAuthenticationFilter tokenAuthenticationFilter,
                                 DefaultUserService userService, ExceptionHandlerFilter exceptionHandlerFilter) {
        this.passwordEncoder = passwordEncoder;
        this.tokenAuthenticationFilter = tokenAuthenticationFilter;
        this.userService = userService;
        this.exceptionHandlerFilter = exceptionHandlerFilter;
    }

    @Override
    public void configure(AuthenticationManagerBuilder authentication) throws Exception {
        authentication
                .userDetailsService(userService)
                .passwordEncoder(passwordEncoder);
    }

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors()
                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/task/*", "/auth/*", "/actuator/*", "/admin/*",
                        "/admin/*/*", "/swagger-ui/*", "/swagger-resources/**", "/v2/api-docs")
                .permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        httpSecurity.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        httpSecurity.addFilterBefore(exceptionHandlerFilter, TokenAuthenticationFilter.class);
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @PostConstruct
    private void createAdminUser() {
        var adminUsername = "admin";
        var existingAdmin = userService.findUserByUsername(adminUsername);
        if (existingAdmin == null) {
            userService.signUpUser(
                    new SignUpRequest(adminUsername, adminPassword, adminEmail, Role.ADMIN));
        }
    }
}
