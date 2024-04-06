package dev.api.service;

import dev.api.dto.SigninDto;
import dev.api.dto.SignupDto;
import dev.api.event.events.RegistrationEvent;
import dev.api.model.Role;
import dev.api.model.User;
import dev.api.model.VerificationToken;
import dev.api.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthenticationService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ApplicationEventPublisher publisher;
    private final EmailVerificationService tokenVerificationService;
    private final TwoFactorAuthenticationService twoFactorAuthenticationService;
    
 

    public void signup(SignupDto dto , String url) {
       
        if( userRepository.findByUsername(dto.getUsername()).isPresent() || 
            userRepository.findByEmail(dto.getEmail()).isPresent())
            return ;
        
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        
        User newUser = new User(dto.getUsername() , dto.getEmail() , encodedPassword , Role.ROLE_USER);
        
        userRepository.save(newUser);

        String generatedverificationToken = UUID.randomUUID().toString();
        
        VerificationToken verificationToken = new VerificationToken(generatedverificationToken, LocalDateTime.now(),LocalDateTime.now().plusMinutes(15), newUser);
        tokenVerificationService.saveVerificationToken(verificationToken);

        publisher.publishEvent(new RegistrationEvent(newUser , url , generatedverificationToken));
    }


    public String signin(SigninDto dto ) {
        try {
            Authentication authenticatedUser = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getUsername(),dto.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
            // System.out.println(authenticatedUser);
            User user = (User) authenticatedUser.getPrincipal();
            
            if (user.isMfaEnabled()) 
            {
                user.setEnabled(true);
                user.setSecret(twoFactorAuthenticationService.generateNewSecret());
                userRepository.save(user);
                return twoFactorAuthenticationService.generateQrCodeImageUri(user.getSecret());
            }

            String jwt = jwtService.generateToken(user);

            return jwt;

        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            return null;
        }
    }

   
    



}
