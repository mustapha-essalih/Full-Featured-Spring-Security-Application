package api.dev.user.dto;

public class UpdatePasswordDto {

    private String currentPassword;

    private String newPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

}
