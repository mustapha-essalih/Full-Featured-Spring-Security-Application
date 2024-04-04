package dev.api.config;
 import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.security.Security;
import java.util.logging.Logger;

 
import jakarta.transaction.TransactionScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import dev.api.service.JwtService;
import dev.api.service.UserService;
import io.jsonwebtoken.JwtException;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  
    private JwtService jwtService;
    private UserService userService;

    public JwtAuthenticationFilter(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException 
    {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
     
        if (authHeader == null) // if request not for jwt authentication
        {
            filterChain.doFilter(request, response);
            return;
        }
        try {

            if (authHeader.isEmpty() || !authHeader.startsWith("Bearer ")) {
                throw new JwtException("jwt error");
            }
        
            jwt = authHeader.substring(7);
            username = jwtService.extractUserName(jwt);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) // because 
            {
                UserDetails userDetails = userService.loadUserByUsername(username);
                if (jwtService.isTokenValid(jwt, userDetails)) 
                {
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        context.setAuthentication(authToken);
                        SecurityContextHolder.setContext(context);
                    }
                else
                {
                    throw new JwtException("jwt error");
                }
            }
            else
            {
                throw new JwtException("jwt error");
            }
            
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
            response.setStatus(403);
            response.setContentType("application/json"); // Set content type (optional)
            response.getWriter().write("invalid jwt");
            return;
        }   
        filterChain.doFilter(request, response);
        
    }
}
