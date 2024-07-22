package api.dev.user;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import api.dev.user.dto.UpdatePasswordDto;
import jakarta.validation.Valid;


@PreAuthorize("hasAnyRole('ADMIN','USER')")
@RequestMapping("/api/user")
@RestController
public class UserController {

    private UsersService userService;

    

    public UserController(UsersService userService) {
        this.userService = userService;
    }



    @GetMapping("/")
    public String whoIm() 
    {
        return "user";
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
