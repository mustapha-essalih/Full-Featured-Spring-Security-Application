package dev.api.service;

import dev.api.dto.SigninDto;
import dev.api.dto.SignupDto;
import dev.api.dto.UpdatePassword;
import dev.api.event.events.RegistrationEvent;
import dev.api.model.Role;
import dev.api.model.User;
import dev.api.model.VerificationToken;
import dev.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthenticationService {
    
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ApplicationEventPublisher publisher;
    private final EmailVerificationService tokenVerificationService;
    private final TwoFactorAuthenticationService twoFactorAuthenticationService;
    private final PasswordEncoder encoder;

 

    public void signup(SignupDto dto , String url) {
       
        if( userRepository.findByUsername(dto.getUsername()).isPresent() || 
            userRepository.findByEmail(dto.getEmail()).isPresent())
            return ;
        
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        
        User newUser = new User(dto.getUsername() , dto.getEmail() , encodedPassword , dto.getRole());
        
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

    public ResponseEntity<String>  updatePassword(UpdatePassword dto , String username)
    {
        User user = (User) userService.loadUserByUsername(username);// downcast
        
        if(!encoder.matches(dto.getCurrentPassword(), user.getPassword()))
        {
            return ResponseEntity.status(403).body("incorrect current password");
        }

        user.setPassword(encoder.encode(dto.getNewPassword()));
        
        userRepository.save(user);
        
        return ResponseEntity.status(HttpStatus.OK).body("password updated");
    }
}
