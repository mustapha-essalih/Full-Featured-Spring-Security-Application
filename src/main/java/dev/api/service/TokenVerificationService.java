package dev.api.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import dev.api.model.VerificationToken;
import dev.api.repository.VerificationTokenRepository;
 
@Service
public class TokenVerificationService {

    private VerificationTokenRepository tokenRepository;

    public TokenVerificationService(VerificationTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public void saveVerificationToken(VerificationToken  token) {
            
        tokenRepository.save(token);
    }

    public String verifyEmail(String token) {
       
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
}
