package com.stockcomp.configuration;

import com.stockcomp.controller.common.ExceptionHandlerFilter;
import com.stockcomp.controller.common.TokenAuthenticationFilter;
import com.stockcomp.service.user.DefaultUserService;
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

    private final DefaultUserService defaultUserService;
    private final TokenAuthenticationFilter tokenAuthenticationFilter;
    private final ExceptionHandlerFilter exceptionHandlerFilter;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfiguration(PasswordEncoder passwordEncoder, TokenAuthenticationFilter tokenAuthenticationFilter,
                                 DefaultUserService defaultUserService, ExceptionHandlerFilter exceptionHandlerFilter) {
        this.passwordEncoder = passwordEncoder;
        this.tokenAuthenticationFilter = tokenAuthenticationFilter;
        this.defaultUserService = defaultUserService;
        this.exceptionHandlerFilter = exceptionHandlerFilter;
    }

    @Override
    public void configure(AuthenticationManagerBuilder authentication) throws Exception {
        authentication
                .userDetailsService(defaultUserService)
                .passwordEncoder(passwordEncoder);
    }

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors()
                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/task/*","/auth/*", "/actuator/*","/admin/*",
                        "/admin/*/*","/swagger-ui.html")
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
}
