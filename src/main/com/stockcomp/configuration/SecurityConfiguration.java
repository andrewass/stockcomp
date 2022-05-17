package com.stockcomp.configuration;

import com.stockcomp.producer.common.ExceptionHandlerFilter;
import com.stockcomp.producer.common.TokenAuthenticationFilter;
import com.stockcomp.user.entity.Role;
import com.stockcomp.request.SignUpRequest;
import com.stockcomp.authentication.service.CustomOAuth2UserService;
import com.stockcomp.user.service.DefaultUserService;
import com.stockcomp.authentication.service.OAuth2LoginSuccessHandler;
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
import org.springframework.web.cors.CorsConfiguration;

import javax.annotation.PostConstruct;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final DefaultUserService userService;
    private final CustomOAuth2UserService oAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final TokenAuthenticationFilter tokenAuthenticationFilter;
    private final ExceptionHandlerFilter exceptionHandlerFilter;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.email}")
    private String adminEmail;

    public SecurityConfiguration(PasswordEncoder passwordEncoder, TokenAuthenticationFilter tokenAuthenticationFilter,
                                 DefaultUserService userService, CustomOAuth2UserService oAuth2UserService,
                                 OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler, ExceptionHandlerFilter exceptionHandlerFilter) {
        this.passwordEncoder = passwordEncoder;
        this.tokenAuthenticationFilter = tokenAuthenticationFilter;
        this.userService = userService;
        this.oAuth2UserService = oAuth2UserService;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
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
        httpSecurity
                .cors().configurationSource(request -> createCorsConfiguration())
                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/task/*", "/auth/*", "/actuator/*", "/admin/**", "/login/oauth2/code/google",
                        "/swagger-ui/*", "/swagger-resources/**", "/v2/api-docs", "investment-order/*")
                .permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2Login()
                .userInfoEndpoint()
                .userService(oAuth2UserService)
                .and()
                .successHandler(oAuth2LoginSuccessHandler)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        httpSecurity.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        httpSecurity.addFilterBefore(exceptionHandlerFilter, TokenAuthenticationFilter.class);
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    private CorsConfiguration createCorsConfiguration() {
        var cors = new CorsConfiguration();
        cors.addAllowedOrigin("http://localhost:8000");
        cors.addAllowedOrigin("http://stockclient-service:80");
        cors.addAllowedOrigin("http://localhost:80");
        cors.setAllowCredentials(true);
        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        cors.setAllowedHeaders(List.of("Origin", "Access-Control-Allow-Origin", "Access-Control-Expose-Headers",
                "Content-Type", "Accept", "Authorization", "Origin,Accept", "X-Requested-With",
                "Access-Control-Request-Method", "Access-Control-Request-Headers", "Range"));
        cors.setExposedHeaders(List.of("Content-Range"));

        return cors;
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
