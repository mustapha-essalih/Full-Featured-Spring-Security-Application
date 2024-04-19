package dev.api.dto.response;

import java.util.Date;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class JwtResponse {
    
    Date issuedAt;
    Date expiration; 
    String jwt;
   
    
    public JwtResponse( String jwt, Date issuedAt, Date expiration) {
        this.issuedAt = issuedAt;
        this.expiration = expiration;
        this.jwt = jwt;
    }
}
