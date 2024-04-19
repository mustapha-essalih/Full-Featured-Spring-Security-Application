package dev.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class OtpDto {
    
    @NotBlank(message = "password shouldn't be empty")
    @NotNull(message = "username shouldn't be null")
    private String username;
    
    @NotNull(message = "otp shouldn't be null")
    private String otp;
}
