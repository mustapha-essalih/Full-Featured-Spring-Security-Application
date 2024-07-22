package api.dev.user;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import api.dev.user.dto.UpdatePasswordDto;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;


@CrossOrigin
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin")
@RestController
public class AdminController {
    

    private UsersService userService;

    
    public AdminController(UsersService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String whoIm() 
    {
        return "admin";
    }
    

    @PatchMapping("/update-password")
    public  ResponseEntity<?>  updatePassword(@RequestBody UpdatePasswordDto dto , Principal  principal){
        return userService.updatePassword(dto , principal.getName());
    } 

    @PutMapping("/enable-2fa")
    public ResponseEntity<Void> enable2Fa(Principal principal) 
    {
        return userService.enable2Fa(principal.getName());    
    }

    @PutMapping("/disable-2fa")
    public ResponseEntity<Void> disable2Fa(Principal principal) 
    {
        return userService.disable2Fa(principal.getName());    
    }
    
    
}
