package dev.api.event.events;

import org.springframework.context.ApplicationEvent;

import dev.api.model.User;

public class RegistrationEvent extends ApplicationEvent{

    private User user;
    private String url;
    private String generatedJwtToken;

    
    public RegistrationEvent(User user, String url , String generatedJwtToken) 
    {
        super(user);
        this.user = user;
        this.url = url;
        this.generatedJwtToken = generatedJwtToken;
    }
    
    public String getGeneratedJwtToken() {
        return generatedJwtToken;
    }

    public void setGeneratedJwtToken(String generatedJwtToken) {
        this.generatedJwtToken = generatedJwtToken;
    }
    
    public User getUser() {
        return user;
    }

    public String getUrl() {
        return url;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
