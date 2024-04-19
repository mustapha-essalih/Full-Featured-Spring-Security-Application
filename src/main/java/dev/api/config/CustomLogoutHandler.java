package dev.api.config;

import java.io.IOException;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import dev.api.model.JwtToken;
import dev.api.repository.JwtTokenRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
public class CustomLogoutHandler implements LogoutHandler {

    private final JwtTokenRepository jwtTokenRepository;


    
    public CustomLogoutHandler(JwtTokenRepository jwtTokenRepository) {
        this.jwtTokenRepository = jwtTokenRepository;
    }



    @Override
    public void logout(HttpServletRequest request,HttpServletResponse response,Authentication authentication) {
        try {
            String authHeader = request.getHeader("Authorization");
    
            if (authHeader.isEmpty() || !authHeader.startsWith("Bearer ")) {
                throw new JwtException("jwt error");
            }
    
            String token = authHeader.substring(7);
            JwtToken storedToken = jwtTokenRepository.findByToken(token);
    
            if(storedToken != null) 
            {
                storedToken.set_logged_out(true);
                jwtTokenRepository.save(storedToken);
                SecurityContextHolder.clearContext();

            }
            else{
                throw new JwtException("jwt error");
 
            }
        } catch (Exception e) {
            response.setStatus(403);
            response.setContentType("application/json"); // Set content type (optional)
            try {
                response.getWriter().write("invalid jwt");
            } catch (IOException e1) {
                
                return;
            }
        }
    }
}