package dev.api.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import dev.api.event.events.RegistrationEvent;
import dev.api.model.User;
import dev.api.model.VerificationToken;
import dev.api.repository.UserRepository;
import dev.api.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
 
@RequiredArgsConstructor
@Service
public class EmailVerificationService {

    private final VerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher publisher;


    public void saveVerificationToken(VerificationToken  token) {
            
        tokenRepository.save(token);
    }

    public String emailVerification(String token) {
       
        VerificationToken verificationToken = tokenRepository.findByToken(token);

        if (verificationToken == null) 
            return null;
        

        if (verificationToken.getUser().isEnabled())
        {
            return "This account has already been verified, please, login.";
        }

        LocalDateTime expiredAt = verificationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            return "token expired";
        }

        verificationToken.getUser().setEnabled(true);

        tokenRepository.save(verificationToken);

        return "Email verified successfully. Now you can login to your account";
    }

	public String resendEmail(String username , String url) {
		
        User user =  userRepository.findByUsername(username).orElse(null);

        if(user == null){
            return null;
        }

        if (user.isEnabled()) {
            return "This account has already been verified, please, login.";
        }

        String generatedverificationToken = UUID.randomUUID().toString();
        
        VerificationToken verificationToken = new VerificationToken(generatedverificationToken, LocalDateTime.now(),LocalDateTime.now().plusMinutes(15), user);
        
        this.saveVerificationToken(verificationToken);

        publisher.publishEvent(new RegistrationEvent(user , url , generatedverificationToken));
        return "A new verification link has been sent to your email,please, check to activate your account";
	}



}
