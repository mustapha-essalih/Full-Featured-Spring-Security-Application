package dev.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UpdatePassword {
    
    
    @NotEmpty(message = "password shouldn't be empty")
    @NotNull(message = "password shouldn't be null")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@*#$%^&+=])(?=\\S+$).{8,20}$" , message="invalid password")
    private String currentPassword;

    @NotEmpty(message = "password shouldn't be empty")
    @NotNull(message = "password shouldn't be null")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@*#$%^&+=])(?=\\S+$).{8,20}$" , message="invalid password")
    private String newPassword;
}
