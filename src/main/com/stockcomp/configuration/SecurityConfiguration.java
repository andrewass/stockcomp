package com.stockcomp.configuration;

import com.stockcomp.controller.common.TokenAuthenticationFilter;
import com.stockcomp.service.CustomUserService;
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

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final CustomUserService customUserService;
    private final TokenAuthenticationFilter requestFilter;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfiguration(PasswordEncoder passwordEncoder, TokenAuthenticationFilter requestFilter,
                                 CustomUserService customUserService) {
        this.passwordEncoder = passwordEncoder;
        this.requestFilter = requestFilter;
        this.customUserService = customUserService;
    }

    @Override
    public void configure(AuthenticationManagerBuilder authentication) throws Exception {
        authentication
                .userDetailsService(customUserService)
                .passwordEncoder(passwordEncoder);
    }

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors()
                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/auth/*", "/stock/*", "/scheduler/*", "/actuator/*", "/contest/*","/investment-order/*")
                .permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        httpSecurity.addFilterBefore(requestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
