package dev.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ResetPasswordDto 
{
    @NotNull(message = "oldPassword shouldn't be null")
    @NotEmpty(message = "oldPassword shouldn't be empty")
    private String oldPassword;
    
    @NotNull(message = "newPassword shouldn't be null")
    @NotEmpty(message = "newPassword shouldn't be empty")
    private String newPassword;
}
