package dev.api.event.events;

import org.springframework.context.ApplicationEvent;

import dev.api.model.User;
import lombok.Getter;

public class ResetPasswordEvent extends ApplicationEvent {

    private User user;
    private String link;
    private String passwordResetToken;

    public ResetPasswordEvent(User user, String link , String passwordResetToken) {
        super(user);
        this.user = user;
        this.link = link;
        this.passwordResetToken = passwordResetToken;
    }

    public User getUser() {
        return user;
    }

    public String getLink() {
        return link;
    }

    public String getPasswordResetToken() {
        return passwordResetToken;
    }

}
