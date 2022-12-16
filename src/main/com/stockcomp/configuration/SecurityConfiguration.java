package com.stockcomp.configuration;

import com.stockcomp.user.service.DefaultUserService;
import com.stockcomp.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;

import javax.annotation.PostConstruct;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserService userService;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.email}")
    private String adminEmail;

    public SecurityConfiguration(DefaultUserService userService) {
        this.userService = userService;
    }

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors().configurationSource(request -> createCorsConfiguration())
                .and()
                .csrf().disable()
                .authorizeRequests(authorize -> authorize
                        .antMatchers("/task/*", "/actuator/*","/user/*",
                                "/swagger-ui/*", "/swagger-resources/**", "/v2/api-docs")
                        .permitAll()
                        .anyRequest().authenticated()
                ).oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    private CorsConfiguration createCorsConfiguration() {
        var cors = new CorsConfiguration();
        cors.addAllowedOrigin("http://localhost:8000");
        cors.addAllowedOrigin("http://localhost:3000");
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
            /*
            userService.signUpUser(
                    new SignUpRequest(adminUsername, adminPassword, adminEmail, Role.ADMIN));

             */
        }
    }
}
