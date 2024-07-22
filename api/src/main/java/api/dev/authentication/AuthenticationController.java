package api.dev.authentication;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import api.dev.authentication.dto.OtpDto;
import api.dev.authentication.dto.ResetPasswordDto;
import api.dev.authentication.dto.SigninDto;
import api.dev.authentication.dto.SignupDto;
import api.dev.email.EmailService;
import api.dev.exceptions.ResourceNotFoundException;
import api.dev.security.JwtService;
import api.dev.user.dto.JwtResponse;
import jakarta.servlet.http.HttpServletRequest;


@CrossOrigin
@RequestMapping("/api/auth")
@RestController
public class AuthenticationController {
    

    private AuthenticationService authenticationService;
    private EmailService emailService;
    private TwoFactorAuthenticationService twoFactorAuthenticationService;
    private JwtService jwtService;

    

    public AuthenticationController(AuthenticationService authenticationService, EmailService emailService,
            TwoFactorAuthenticationService twoFactorAuthenticationService, JwtService jwtService) {
        this.authenticationService = authenticationService;
        this.emailService = emailService;
        this.twoFactorAuthenticationService = twoFactorAuthenticationService;
        this.jwtService = jwtService;
    }



    @PostMapping("/signup")
    ResponseEntity<String> signup(@RequestBody SignupDto dto , HttpServletRequest request)
    { 
        return authenticationService.signup(dto , getUrlOfRequest(request));
    }

    @PostMapping("/signin")
    ResponseEntity<String> signin(@RequestBody SigninDto dto)
    {
        return authenticationService.signin(dto);
    }


    @GetMapping("/email-verification")
    public ResponseEntity<String> emailVerification(@RequestParam String token) throws ResourceNotFoundException
    {
        return emailService.emailVerification(token);
    }

    @PostMapping("/resend-email-verification")
    ResponseEntity<String> resendEmailVerification(@RequestParam String email,  HttpServletRequest request) throws ResourceNotFoundException
    {
        return authenticationService.resendEmail(email, getUrlOfRequest(request));
    }

    @PostMapping("/password-reset-request")
    ResponseEntity<String> passwordReset(@RequestParam String email ,  HttpServletRequest request) throws ResourceNotFoundException
    {
        return authenticationService.passwordReset(email , getUrlOfRequest(request));
    }

    @PostMapping("/reset-password")  
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto dto, @RequestParam("token") String token) throws ResourceNotFoundException{
        
        return authenticationService.resetPassword(dto , token);
    }


    @PostMapping("/verify-OTP")
    public ResponseEntity<JwtResponse> verifyOTP(@RequestBody OtpDto dto) throws ResourceNotFoundException 
    {
        return twoFactorAuthenticationService.verifyCode(dto);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<String> refreshToken(@RequestParam String refreshToken) 
    {    
        return jwtService.refreshToken(refreshToken);
    }

    
    private String getUrlOfRequest(HttpServletRequest request){
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
}
