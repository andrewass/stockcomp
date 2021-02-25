package com.stockcomp.controller.filter;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtRequestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) {

        /*
        var header = httpServletRequest.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            var token = header.split(" ")[1];
            var username = extractUsername(token);

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                var userDetails = userService.loadUserByUsername(username);
                if (tokenIsValid(token, userDetails)) {
                    var authToken = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities);
                    authToken.details = WebAuthenticationDetailsSource().buildDetails(httpServletRequest);
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }
        chain.doFilter(request, response);

         */
    }
}
