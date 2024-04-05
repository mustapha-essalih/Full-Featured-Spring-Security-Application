package dev.api.event.listiners;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import dev.api.event.events.RegistrationEvent;
import dev.api.model.User;
import dev.api.model.VerificationToken;
import dev.api.repository.VerificationTokenRepository;
import dev.api.service.EmailVerificationService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
                                                                    // event who will listiner it
public class EmailVerificationListinner implements ApplicationListener<RegistrationEvent>{

    private  final JavaMailSender mailSender;
    private  User user;

    @Value("${spring.mail.username}")
    private String from; 
    
    @Override
    public void onApplicationEvent(RegistrationEvent event) {
        
        user = event.getUser();

        String url = event.getUrl()+ "/api/auth/emailVerification?token=" + event.getGeneratedverificationToken();

        try {
            sendVerificationEmail(url);
            
        } catch (Exception e) {
            System.out.println("email error.");
            // TODO: handle exception
        }

    }

    @Async
    public void sendVerificationEmail(String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Email Verification";
        String senderName = "User Registration Portal Service";
        String mailContent = "<p> Hi, "+ user.getUsername()+ ", </p>"+
                "<p>Thank you for registering with us,"+"" +
                "Please, follow the link below to complete your registration.</p>"+
                "<a href=\"" + url + "\">Verify your email to activate your account</a>"+
                "<p> Thank you <br> Users Registration Portal Service";
        MimeMessage message = mailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom(from, senderName);
        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        mailSender.send(message);
    }
}
