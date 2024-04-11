package dev.api.event.events;

import org.springframework.context.ApplicationEvent;

import dev.api.model.User;

public class RegistrationEvent extends ApplicationEvent{

    private User user;
    private String url;
    private String generatedverificationToken;

    
    public RegistrationEvent(User user, String url , String generatedverificationToken) 
    {
        super(user);
        this.user = user;
        this.url = url;
        this.generatedverificationToken = generatedverificationToken;
    }
    
    public String getGeneratedverificationToken() {
        return generatedverificationToken;
    }

    public void setGeneratedverificationToken(String generatedverificationToken) {
        this.generatedverificationToken = generatedverificationToken;
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
