package dev.api.event.listiners;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import dev.api.event.events.ResetPasswordEvent;
import dev.api.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ResetPasswordLisner implements ApplicationListener<ResetPasswordEvent>{
    
    
    private  final JavaMailSender mailSender;
    private  User user;
    
    @Value("${spring.mail.username}")
    private String from; 
    
    @Override
    public void onApplicationEvent(ResetPasswordEvent event) {

        user = event.getUser();

        try {
            sendPasswordResetVerificationEmail(event.getLink());
            
        } catch (Exception e) {
            System.out.println("email error.");
            // TODO: handle exception
        }
    }


      public void sendPasswordResetVerificationEmail(String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Password Reset Request Verification";
        String senderName = "User Registration Portal Service";
        String mailContent = "<p> Hi, "+ user.getUsername()+ ", </p>"+
                "<p><b>You recently requested to reset your password,</b>"+"" +
                "Please, follow the link below to complete the action.</p>"+
                "<a href=\"" + url+ "\">Reset password</a>"+
                "<p> Users Registration Portal Service";
        MimeMessage message = mailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom(from, senderName);
        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        mailSender.send(message);
    }
    
}
