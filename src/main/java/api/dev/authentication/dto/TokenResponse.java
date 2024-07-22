package api.dev.authentication.dto;

public class TokenResponse {

    private String jwt;
    private String refreshToken;

    public TokenResponse(String jwt, String refreshToken) {
        this.jwt = jwt;
        this.refreshToken = refreshToken;
    }

    @Override
    public String toString() {
        return " jwt=" + jwt + "\n refreshToken=" + refreshToken;
    }

    
}
