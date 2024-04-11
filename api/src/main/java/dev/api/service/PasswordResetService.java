package dev.api.service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dev.api.dto.ResetPasswordDto;
import dev.api.event.events.ResetPasswordEvent;
import dev.api.model.ResetTokenPassword;
import dev.api.model.User;
import dev.api.repository.ResetTokenPasswordRepository;
import dev.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final ResetTokenPasswordRepository repository;
    private final ApplicationEventPublisher publisher;
    private final PasswordEncoder passwordEncoder;

    public String passwordResetRequest(String username , String url) 
    {
        User user =  userRepository.findByUsername(username).orElse(null);

        if(user == null)
            return null;

        if (!user.isEnabled()) 
        {
            return "should verify you acount in first. resend email verification.";    
        }

        String passwordResetToken = UUID.randomUUID().toString();
    
        ResetTokenPassword resetTokenPassword = new ResetTokenPassword(passwordResetToken, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), user);

        repository.save(resetTokenPassword);
 
        String link = passwordResetEmailLink(url , passwordResetToken);
        publisher.publishEvent(new ResetPasswordEvent(user, link , passwordResetToken ));
        
        return "check your email, Click the link to reset your password";
    }
    
    
    public String resetPassword(ResetPasswordDto dto, String token) {
        
        ResetTokenPassword resetTokenPassword = repository.findByToken(token);

        if (resetTokenPassword == null) 
            return null;    


        LocalDateTime expiredAt = resetTokenPassword.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) 
            return null;

        User user = resetTokenPassword.getUser();

        if(!passwordEncoder.matches(dto.getOldPassword(), user.getPassword()))
            return "Incorrect old passord";

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));

        resetTokenPassword.setUser(user);

        repository.save(resetTokenPassword);

        return "Password has been reset successfully";
    }
    
    private String passwordResetEmailLink(String url , String passwordResetToken){

        return url + "/api/auth/reset-password?token=" + passwordResetToken;
    }
}
