package api.dev.security;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import api.dev.exceptions.ResourceNotFoundException;
import api.dev.user.model.JwtToken;
import api.dev.user.model.User;
import api.dev.user.repository.JwtTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomLogoutHandler implements LogoutHandler {

    private final JwtTokenRepository jwtTokenRepository;

    public CustomLogoutHandler(JwtTokenRepository jwtTokenRepository) {
        this.jwtTokenRepository = jwtTokenRepository;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    
        String authHeader = request.getHeader("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        try {
            User user = (User)authentication.getPrincipal();

            List<JwtToken> jwtTokenOfUser = jwtTokenRepository.findAllValidTokenByUser(user.getUserId());

            jwtTokenOfUser.forEach(t-> {
                t.setIs_logged_out(true);
            });

            jwtTokenRepository.saveAll(jwtTokenOfUser);

            
        } catch (Exception e) {
        }
    }
    

}
