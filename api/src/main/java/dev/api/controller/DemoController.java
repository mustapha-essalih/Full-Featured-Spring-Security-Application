package dev.api.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
    

    @GetMapping("/csrfToken")
    CsrfToken getCsrfToken(CsrfToken token){
        return token;
    }


}
