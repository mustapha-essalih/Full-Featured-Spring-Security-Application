package api.dev.user.model;

import java.util.Date;

import jakarta.persistence.Entity;

@Entity
public class JwtToken extends Token { 


    private boolean is_logged_out;
    
    public boolean revoked;

    
    public JwtToken() 
    {
        is_logged_out = false;
    }
    
    public JwtToken(String token, Date createdAt ,  Date expiresAt, boolean is_logged_out, User user)
    {
        super.setToken(token);
        super.setCreatedAt(createdAt);
        super.setExpiresAt(expiresAt);
        super.setCreatedAt(createdAt);
        this.is_logged_out = is_logged_out;
        super.setUser(user);
    }

    public boolean isIs_logged_out() {
        return is_logged_out;
    }

    public void setIs_logged_out(boolean is_logged_out) {
        this.is_logged_out = is_logged_out;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    
 
    

}
