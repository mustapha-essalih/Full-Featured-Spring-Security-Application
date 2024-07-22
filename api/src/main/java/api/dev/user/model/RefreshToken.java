package api.dev.user.model;

import java.util.Date;

import jakarta.persistence.Entity;

@Entity
public class RefreshToken extends Token {
    
    
    public RefreshToken() {}

    
    public RefreshToken(String token,Date createdAt ,  Date expiresAt, User user)
    {
        super.setToken(token);
        super.setCreatedAt(createdAt);
        super.setExpiresAt(expiresAt);
        super.setCreatedAt(createdAt);
        super.setUser(user);
    }
}
