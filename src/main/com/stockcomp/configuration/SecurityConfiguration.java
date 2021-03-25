package com.stockcomp.configuration;

import com.stockcomp.controller.filter.JwtRequestFilter;
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
    private final JwtRequestFilter requestFilter;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfiguration(PasswordEncoder passwordEncoder, JwtRequestFilter requestFilter,
                                 CustomUserService customUserService) {
        this.passwordEncoder = passwordEncoder;
        this.requestFilter = requestFilter;
        this.customUserService = customUserService;
    }

    /**
     * Configuring the authentication process
     */
    @Override
    public void configure(AuthenticationManagerBuilder authentication) throws Exception {
        authentication
                .userDetailsService(customUserService)
                .passwordEncoder(passwordEncoder);
    }

    /**
     * Configuring the authorization process
     */
    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors()
                .and()
                .csrf().disable()
                //TODO: Update permissions
                //.authorizeRequests()
                //.antMatchers("/*","/*/*","/auth/*", "/contest/*", "/stock/*", "/scheduler/*","/actuator/*")
                //.permitAll()
                //.anyRequest().authenticated()
                //.and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        httpSecurity.addFilterBefore(requestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
