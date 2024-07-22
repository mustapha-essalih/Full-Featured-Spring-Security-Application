package api.dev.authentication.dto;


public class ResetPasswordDto 
{
    private String oldPassword;
    
    private String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    
}
