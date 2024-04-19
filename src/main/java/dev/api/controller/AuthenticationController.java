package dev.api.controller;

import dev.api.dto.request.OtpDto;
import dev.api.dto.request.RefreshTokenRequest;
import dev.api.dto.request.ResetPasswordDto;
import dev.api.dto.request.SigninDto;
import dev.api.dto.request.SignupDto;
import dev.api.dto.request.UpdatePassword;
import dev.api.dto.request.UserDto;
import dev.api.service.AuthenticationService;
import dev.api.service.EmailVerificationService;
import dev.api.service.JwtService;
import dev.api.service.PasswordResetService;
import dev.api.service.TwoFactorAuthenticationService;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;


@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final EmailVerificationService emailVerificationService;
    private final PasswordResetService passwordResetService; 
    private final TwoFactorAuthenticationService twoFactorAuthenticationService;
    private final JwtService jwtService;


    @PostMapping("/signup")
    ResponseEntity<String> signup(@RequestBody @Valid SignupDto dto , HttpServletRequest request)
    { 
        authenticationService.signup(dto , getUrlOfRequest(request));
        return ResponseEntity.status(HttpStatus.OK).body("Signup successful,  Please, check your email for to complete your registration");
    }

    @PostMapping("/signin")
    ResponseEntity<?> signin(@RequestBody SigninDto dto, HttpServletResponse res)
    {
        return authenticationService.signin(dto);

        // if (response != null) 
        // {
        //     // Cookie cookie = new Cookie("access_token", response);
        //     // cookie.setHttpOnly(true);
        //     // response.addCookie(cookie);

        //     return response;    
        // }
        // return ResponseEntity.status(401).build();
    }

    
    @GetMapping("/emailVerification")
    public ResponseEntity<String> emailVerification(@RequestParam String token)
    {
        String response = emailVerificationService.emailVerification(token);
        
        if (response == null) 
        {
            return  ResponseEntity.status(403).body("token not found");
        }
        else if(response.equals("token expired"))
        {
            return  ResponseEntity.status(403).body(response);
        }    
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/resendEmail")
    ResponseEntity<String> resendEmailVerification(@RequestBody UserDto dto,  HttpServletRequest request)
    {
        String response = emailVerificationService.resendEmail(dto.getUsername(), getUrlOfRequest(request));
        if (response == null) 
        {
            return ResponseEntity.status(403).build();    
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/password-reset-request")
    ResponseEntity<String> passwordResetRequest(@RequestBody UserDto dto ,  HttpServletRequest request)
    {
        String response = passwordResetService.passwordResetRequest(dto.getUsername() , getUrlOfRequest(request));
        if (response == null) 
        {
            return ResponseEntity.status(403).body("user not found");
        }
        if(response.equals("should verify you acount in first. resend email verification."))
            return ResponseEntity.status(403).body(response);
            
        return ResponseEntity.ok(response); // should redirect the user to a page for enter old and new password

    }

    @PostMapping("/reset-password")  
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto dto, @RequestParam("token") String token){
        
        String response = passwordResetService.resetPassword(dto , token);
        if (response == null) 
        {
            return ResponseEntity.status(403).body("Invalid password reset token");
        }
        if (response.equals("Incorrect old passord")) 
        {
            return ResponseEntity.status(403).body("Incorrect old passord");    
        }

        return ResponseEntity.ok(response);
    }

    
    @PostMapping("/verifyOTP")
    public ResponseEntity<?> verifyOTP(@RequestBody OtpDto dto) 
    {
        var jwt = twoFactorAuthenticationService.verifyCode(dto);
        return ResponseEntity.ok(jwt);
    }

    @PatchMapping("/updatepassword")
    public  ResponseEntity<?>  updatePassword(@RequestBody @Valid UpdatePassword dto , Principal  client){
        return authenticationService.updatePassword(dto , client.getName());
    } 

    @PostMapping("/refreshToken")
    public ResponseEntity<String> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) 
    {    
        return jwtService.refreshToken(refreshTokenRequest.getToken());
    }

    private String getUrlOfRequest(HttpServletRequest request){
    
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
}


