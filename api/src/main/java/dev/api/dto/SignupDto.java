package dev.api.dto;

import dev.api.dto.validation.ValidPassword;
import dev.api.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.ToString;

@Getter
public class SignupDto {

    @NotBlank(message = "username shouldn't be empty")
    @NotNull(message = "username shouldn't be null")
    private String username;

    @Email(message = "invalid email")
    private String email;

    @NotEmpty(message = "password shouldn't be empty")
    @NotNull(message = "password shouldn't be null")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@*#$%^&+=])(?=\\S+$).{8,20}$" , message="invalid password")
    private String password;

    @NotNull(message = "role shouldn't be null, (ROLE_ADMIN, ROLE_USER)")
    private Role role;
}
