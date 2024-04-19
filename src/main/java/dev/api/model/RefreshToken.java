package dev.api.model;

import java.util.Date;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

    @Override
    public String toString() {
        return "RefreshToken [id=" + super.getId() + ", token=" + super.getToken() + ", createdAt=" + super.getCreatedAt() + ", expiresAt=" + super.getExpiresAt()
                + ", user=" + super.getUser() + "]";
    }
 
}
