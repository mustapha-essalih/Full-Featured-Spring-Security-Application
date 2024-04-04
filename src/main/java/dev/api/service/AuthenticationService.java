package dev.api.service;

import dev.api.dto.SigninDto;
import dev.api.dto.SignupDto;
import dev.api.event.RegistrationEvent;
import dev.api.model.Role;
import dev.api.model.User;
import dev.api.model.VerificationToken;
import dev.api.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

 
@Service
public class AuthenticationService {
    
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    private ApplicationEventPublisher publisher;
    private TokenVerificationService tokenVerificationService;

    

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JwtService jwtService, ApplicationEventPublisher publisher,
            TokenVerificationService tokenVerificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.publisher = publisher;
        this.tokenVerificationService = tokenVerificationService;
    }


    public void signup(SignupDto dto , String url) {
       
        if( userRepository.findByUsername(dto.getUsername()).isPresent() || 
            userRepository.findByEmail(dto.getEmail()).isPresent())
            return ;
        
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        
        User newUser = new User(dto.getUsername() , dto.getEmail() , encodedPassword , Role.ROLE_USER);
        
        userRepository.save(newUser);

        String generatedverificationToken = UUID.randomUUID().toString();
        System.out.println(generatedverificationToken);

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
            
            String jwt = jwtService.generateToken(user);

            return jwt;


        } catch (Exception e) {
            
            return null;
        }
    }

   
    



}
