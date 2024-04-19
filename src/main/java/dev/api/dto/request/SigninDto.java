package dev.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SigninDto {
    
    @NotBlank(message = "username shouldn't be empty")
    @NotNull(message = "username shouldn't be null")
    private String username;

   
    @NotEmpty(message = "password shouldn't be empty")
    @NotNull(message = "password shouldn't be null")
    private String password;
 
}
