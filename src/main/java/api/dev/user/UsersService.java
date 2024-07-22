package api.dev.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import api.dev.user.dto.UpdatePasswordDto;
import api.dev.user.model.User;
import api.dev.user.repository.UserRepository;
import jakarta.validation.Valid;


@Service
public class UsersService {

    private  UserRepository userRepository;
    private  PasswordEncoder passwordEncoder;
    

    public UsersService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public ResponseEntity<String>  updatePassword(UpdatePasswordDto dto , String email)
    {
        User user = userRepository.findByEmail(email).get();
        
        if(!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword()))
        {
            return ResponseEntity.badRequest().body("incorrect current password");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        
        userRepository.save(user);
        
        return ResponseEntity.status(HttpStatus.OK).body("password updated");
    }


    public ResponseEntity<Void> enable2Fa(String email) {

        User user = userRepository.findByEmail(email).get();

        user.setEnabled(true);

        return ResponseEntity.noContent().build();
    }


    public ResponseEntity<Void> disable2Fa(String email) {
        
        User user = userRepository.findByEmail(email).get();

        user.setEnabled(false);

        return ResponseEntity.noContent().build();
    }

    
}
