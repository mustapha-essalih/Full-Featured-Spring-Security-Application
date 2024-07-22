package api.dev.email;

 
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import api.dev.exceptions.ResourceNotFoundException;
import api.dev.user.model.User;
import api.dev.user.model.VerificationToken;
import api.dev.user.repository.UserRepository;
import api.dev.user.repository.VerificationTokenRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    
    private  VerificationTokenRepository tokenRepository;
    private  UserRepository userRepository;
    private  final JavaMailSender mailSender;
    
    
    @Value("${spring.mail.username}")
    private String from; 
    
    

    public EmailService(VerificationTokenRepository tokenRepository, UserRepository userRepository,JavaMailSender mailSender) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }



    @Async
    public void sendValidationEmail(User user, String url) {
        String subject = "Email Verification";
        String senderName = "User Registration Portal Service";
        String mailContent = "<p> Hi, "+ user.getUsername()+ ", </p>"+
                "<p>Thank you for registering with us,"+"" +
                "Please, follow the link below to complete your registration.</p>"+
                "<a href=\"" + url + "\">Verify your email to activate your account</a>"+
                "<p> Thank you <br> Users Registration Portal Service";
        MimeMessage message = mailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        try {
            messageHelper.setFrom(from, senderName);
            messageHelper.setTo(user.getEmail());
            messageHelper.setSubject(subject);
            messageHelper.setText(mailContent, true);
            mailSender.send(message);
            
        } catch (Exception e) {
            throw new RuntimeException("error in sending email");
        }
    }

 public void sendPasswordResetVerificationEmail(User user, String url) {
        String subject = "Password Reset Request Verification";
        String senderName = "User Registration Portal Service";
        String mailContent = "<p> Hi, " + user.getUsername()+ ", </p>"+
                "<p><b>You recently requested to reset your password,</b>"+"" +
                "Please, follow the link below to complete the action.</p>"+
                "<a href=\"" + url+ "\">Reset password</a>"+
                "<p> Users Registration Portal Service";
        MimeMessage message = mailSender.createMimeMessage();
        try {
            var messageHelper = new MimeMessageHelper(message);
            messageHelper.setFrom(from, senderName);
            messageHelper.setTo(user.getEmail());
            messageHelper.setSubject(subject);
            messageHelper.setText(mailContent, true);
            mailSender.send(message);
            
        } catch (Exception e) {
            throw new RuntimeException("error in sending email");
        }
    }
    


    public ResponseEntity<String> emailVerification(String token) throws ResourceNotFoundException {
        VerificationToken verificationToken = tokenRepository.findByToken(token).orElseThrow(() -> new ResourceNotFoundException("token not found"));

        if (verificationToken.getUser().isEnabled())
            return ResponseEntity.badRequest().body("This account has already been verified, please, login.");

        Date expiredAt = verificationToken.getExpiresAt();

        if (expiredAt.before(expiredAt)) 
            return ResponseEntity.badRequest().body("token expired");


        verificationToken.getUser().setEnabled(true);

        tokenRepository.save(verificationToken);

        return ResponseEntity.ok("Email verified successfully. Now you can login to your account");

    }



   
    


}
