package dev.api.dto;

import lombok.Getter;

@Getter
public class ResetPasswordDto 
{
    private String oldPassword;
    private String newPassword;
}
