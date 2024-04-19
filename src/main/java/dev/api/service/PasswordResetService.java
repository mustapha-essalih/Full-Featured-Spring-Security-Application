package dev.api.service;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dev.api.dto.request.ResetPasswordDto;
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
    
        Date currentDate = new Date();

        // Create a Calendar instance
        Calendar calendar = Calendar.getInstance();

        // Set the calendar to the current date and time
        calendar.setTime(currentDate);

        // Add 15 minutes to the calendar
        calendar.add(Calendar.MINUTE, 15);

        // Get the date 15 minutes later
        Date datePlus15Minutes = calendar.getTime();
        
        ResetTokenPassword resetTokenPassword = new ResetTokenPassword(passwordResetToken, currentDate, datePlus15Minutes, user);

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
