package dev.api.dto;

import lombok.Getter;

@Getter
public class TwoFaResponseDto {
    
    private String jwt;
    private String secretImageUri;


    public TwoFaResponseDto(String jwt, String secretImageUri) {
        this.jwt = jwt;
        this.secretImageUri = secretImageUri;
    }
}
