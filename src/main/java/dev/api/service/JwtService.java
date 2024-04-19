package dev.api.service;

 
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import dev.api.dto.response.JwtResponse;
import dev.api.dto.response.TokenResponse;
import dev.api.model.RefreshToken;
import dev.api.model.User;
import dev.api.repository.RefreshTokenRepository;
import dev.api.utils.enums.TokenType;

@RequiredArgsConstructor
@Service
public class JwtService {

    @Value("${token.secreat.key}")
    private String secretKey;

    @Value("${token.expirationms}")
    private long jwtExpiration; 

    private final RefreshTokenRepository refreshTokenRepository;


    public String extractUserName(String token) {
        
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
        .parser()
        .setSigningKey(generateKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
    }

    public JwtResponse generateToken(User user) 
    {
        Date issuedAt = new Date(System.currentTimeMillis());
        Date expiration = new Date(issuedAt.getTime() + jwtExpiration);
        Map<String, Object> extraClaims = generateExtraClaims(user);

        String jwt = Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(generateKey(), SignatureAlgorithm.HS256)
                .compact();
        return new JwtResponse(jwt, issuedAt , expiration ); 
    }

    private Map<String, Object> generateExtraClaims(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("email", user.getEmail());
        extraClaims.put("id", user.getId());
        extraClaims.put("role", user.getRole().name());
        return extraClaims;
    }

    private Key generateKey(){
        byte[] secreateAsBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(secreateAsBytes);
    }

    public String generateRefreshToken(User user) {
        
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();

        // Set the calendar to the current date
        calendar.setTime(currentDate);

        // Add one week (7 days) to the calendar
        calendar.add(Calendar.DAY_OF_YEAR, 7);

        // Get the date one week later
        Date datePlusOneWeek = calendar.getTime();
        
        String refreshToken = UUID.randomUUID().toString();
        RefreshToken newRefreshToken = new RefreshToken(refreshToken, currentDate , datePlusOneWeek,user);
        refreshTokenRepository.save(newRefreshToken);
        return refreshToken;
    }

    public void verifyExpiration(RefreshToken token) {

        LocalDateTime expiredAt = token.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) 
            throw new RuntimeException(" Refresh token was expired. Please make a new signin request");
    }

    public ResponseEntity<String> refreshToken(String token) { // pass here the refresh token to generate jwt

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElseThrow(() ->  new RuntimeException("token not found"));
        this.verifyExpiration(refreshToken);
        
        User user = refreshToken.getUser();
        String jwt = generateToken(user);
        return ResponseEntity.ok().body(new TokenResponse(jwt, token).toString());
    }

   
}


 