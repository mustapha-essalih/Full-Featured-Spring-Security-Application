package dev.api.dto;

import dev.api.model.Role;
import lombok.Getter;
import lombok.ToString;

@Getter
public class SignupDto {

    private String username;

    private String email;

    private String password;


}
