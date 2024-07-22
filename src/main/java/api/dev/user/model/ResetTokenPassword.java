package api.dev.user.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;


@Table(name = "passwordToken")
@Entity
public class ResetTokenPassword extends Token {

    
    public ResetTokenPassword(){}
    
    public ResetTokenPassword(String token,Date createdAt ,  Date expiresAt, User user)
    {
        super.setToken(token);
        super.setCreatedAt(createdAt);
        super.setExpiresAt(expiresAt);
        super.setCreatedAt(createdAt);
        super.setUser(user);
    }

    

    @Override
    public Date getExpiresAt() {
    
        return super.getExpiresAt();
    }

}
