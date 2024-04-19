package dev.api.config;
 import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
 
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

import dev.api.model.JwtToken;
import dev.api.repository.JwtTokenRepository;
import dev.api.service.JwtService;
import dev.api.service.UserService;
import io.jsonwebtoken.JwtException;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  
    private JwtService jwtService;
    private UserService userService;
    private JwtTokenRepository jwtTokenRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserService userService,
            JwtTokenRepository jwtTokenRepository) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.jwtTokenRepository = jwtTokenRepository;
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
                JwtToken jwtToken = jwtTokenRepository.findByToken(jwt);
                if (jwtService.isTokenValid(jwt, userDetails) && jwtToken.is_logged_out() == false) 
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
