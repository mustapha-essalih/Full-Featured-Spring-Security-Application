package api.dev.authentication.dto;

import api.dev.user.model.Role;

public class SignupDto {

    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private Role role;
    public String getFirstname() {
        return firstname;
    }
    public String getLastname() {
        return lastname;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public Role getRole() {
        return role;
    }

    
}
