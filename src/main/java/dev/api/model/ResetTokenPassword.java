package dev.api.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
@Table(name = "passwordToken")
@Entity
public class ResetTokenPassword extends Token {

    
    public ResetTokenPassword(String token,Date createdAt ,  Date expiresAt, User user)
    {
        super.setToken(token);
        super.setCreatedAt(createdAt);
        super.setExpiresAt(expiresAt);
        super.setCreatedAt(createdAt);
        super.setUser(user);
    }
}
