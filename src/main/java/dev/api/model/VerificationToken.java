package dev.api.model;

import java.util.Date;

import jakarta.persistence.Entity;

@Entity
public class VerificationToken extends Token {

    public VerificationToken(){}

    public VerificationToken(String token,Date createdAt ,  Date expiresAt, User user)
    {
        super.setToken(token);
        super.setCreatedAt(createdAt);
        super.setExpiresAt(expiresAt);
        super.setCreatedAt(createdAt);
        super.setUser(user);
    }
    
}
