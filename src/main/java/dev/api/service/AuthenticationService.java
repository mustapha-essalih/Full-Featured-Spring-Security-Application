package dev.api.service;

import dev.api.dto.request.SigninDto;
import dev.api.dto.request.SignupDto;
import dev.api.dto.request.UpdatePassword;
import dev.api.dto.response.JwtResponse;
import dev.api.dto.response.TokenResponse;
import dev.api.event.events.RegistrationEvent;
import dev.api.model.Role;
import dev.api.model.User;
import dev.api.model.VerificationToken;
import dev.api.model.JwtToken;
import dev.api.repository.JwtTokenRepository;
import dev.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
    private final JwtTokenRepository jwtTokenRepository;
 

    public void signup(SignupDto dto , String url) {
       System.out.println(dto);
        if( userRepository.findByUsername(dto.getUsername()).isPresent() || 
            userRepository.findByEmail(dto.getEmail()).isPresent())
            return ;
        
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        
        User newUser = new User(dto.getUsername() , dto.getEmail() , encodedPassword , Role.ROLE_USER);
        
        userRepository.save(newUser);

        String generateVerificationToken = UUID.randomUUID().toString();
        
        Date currentDate = new Date();

        // Create a Calendar instance
        Calendar calendar = Calendar.getInstance();

        // Set the calendar to the current date and time
        calendar.setTime(currentDate);

        // Add 15 minutes to the calendar
        calendar.add(Calendar.MINUTE, 15);

        // Get the date 15 minutes later
        Date datePlus15Minutes = calendar.getTime();

        // VerificationToken verificationToken = new VerificationToken(generateVerificationToken, currentDate, datePlus15Minutes, newUser);
        
        // tokenVerificationService.saveVerificationToken(verificationToken);

        // publisher.publishEvent(new RegistrationEvent(newUser , url , generateVerificationToken));
    }


    public ResponseEntity<?> signin(SigninDto dto ) {
        
        Authentication authenticatedUser = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getUsername(),dto.getPassword()));
        

        SecurityContextHolder.getContext().setAuthentication(authenticatedUser);

        User user = (User) authenticatedUser.getPrincipal();
        
        if (user.isMfaEnabled()) 
        {
            user.setEnabled(true);
            user.setSecret(twoFactorAuthenticationService.generateNewSecret());
            userRepository.save(user);
            return ResponseEntity.ok().body(twoFactorAuthenticationService.generateQrCodeImageUri(user.getSecret()));
        }
    
        JwtResponse jwt = jwtService.generateToken(user);

        JwtToken jwtToken = new JwtToken(jwt.getJwt(), jwt.getIssuedAt(), jwt.getExpiration(), false, user);

        revokeAllTokenByUser(user.getId());
        jwtTokenRepository.save(jwtToken);
        
        String refreshToken = jwtService.generateRefreshToken(user);
        return ResponseEntity.ok().body(new TokenResponse(jwt.getJwt() , refreshToken).toString());     
    }

    private void revokeAllTokenByUser(Integer userId) 
    {
        List<JwtToken> jwtTokenOfUser = jwtTokenRepository.findAllValidTokenByUser(userId);
        if(jwtTokenOfUser.isEmpty()) {
            return;
        }

        jwtTokenOfUser.forEach(t-> {
            t.set_logged_out(true);
        });

        jwtTokenRepository.saveAll(jwtTokenOfUser);
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
