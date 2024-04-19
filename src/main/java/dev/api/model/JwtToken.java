package dev.api.model;

import java.util.Date;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class JwtToken extends Token { 
    
    private boolean is_logged_out = false;
    
    public JwtToken() {}
    
    public JwtToken(String token,Date createdAt ,  Date expiresAt, boolean is_logged_out, User user)
    {
        super.setToken(token);
        super.setCreatedAt(createdAt);
        super.setExpiresAt(expiresAt);
        super.setCreatedAt(createdAt);
        this.is_logged_out = is_logged_out;
        super.setUser(user);
    }
    
}
