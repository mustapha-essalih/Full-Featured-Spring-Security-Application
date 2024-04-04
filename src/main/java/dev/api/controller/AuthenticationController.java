package dev.api.controller;

import dev.api.dto.SigninDto;
import dev.api.dto.SignupDto;
import dev.api.model.VerificationToken;
import dev.api.service.AuthenticationService;
import dev.api.service.TokenVerificationService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;


@CrossOrigin("*")
@RequestMapping("/api/auth")
@RestController
public class AuthenticationController {

    private AuthenticationService authenticationService;
    private TokenVerificationService tokenVerificationService;
     

    public AuthenticationController(AuthenticationService authenticationService,TokenVerificationService tokenVerificationService) {
        this.authenticationService = authenticationService;
        this.tokenVerificationService = tokenVerificationService;
    }

    @PostMapping("/signup")
    ResponseEntity<String> signup(@RequestBody SignupDto dto , HttpServletRequest request)
    { 
        authenticationService.signup(dto ,  getUrlOfRequest(request));
        return ResponseEntity.status(HttpStatus.OK).body("Signup successful,  Please, check your email for to complete your registration");
    }

    @GetMapping("/verifyEmail")
    public ResponseEntity<String> verifyEmail(@RequestParam String token)
    {
        String response = tokenVerificationService.verifyEmail(token);

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

    @PostMapping("/signin")
    ResponseEntity<Cookie> signin(@RequestBody SigninDto dto, HttpServletResponse response)
    {
        String jwt = authenticationService.signin(dto);
     
        if (jwt != null) 
        {
            Cookie cookie = new Cookie("access_token", jwt);
            cookie.setHttpOnly(true);
            response.addCookie(cookie);

            return ResponseEntity.ok().body(cookie);    
        }
        return ResponseEntity.status(401).build();
    }

    private String getUrlOfRequest(HttpServletRequest request){

        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

    }

}
