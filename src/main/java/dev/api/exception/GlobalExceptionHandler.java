package dev.api.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import io.jsonwebtoken.JwtException;
 
@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity<?> userNotFoundException(BadCredentialsException e) 
    {
        return ResponseEntity.status(401).body(e.getMessage());
    }


    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<?> userNotFoundException(RuntimeException e) 
    {
        return ResponseEntity.status(403).body(e.getMessage());
    }


    @ExceptionHandler(value = DisabledException.class)
    public ResponseEntity<?> DisabledUser(DisabledException e) 
    {
        return ResponseEntity.status(403).body("should active your email, check your email");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleInvalidArgument(MethodArgumentNotValidException ex) {
        
        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMap.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.status(400).body(errorMap);
    }

   

    // should pass correct exception to the method and the correct status code
}
