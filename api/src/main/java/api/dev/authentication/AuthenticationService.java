package api.dev.authentication;

import api.dev.user.model.User;
import api.dev.user.model.VerificationToken;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import api.dev.authentication.dto.ResetPasswordDto;
import api.dev.authentication.dto.SigninDto;
import api.dev.authentication.dto.SignupDto;
import api.dev.authentication.dto.TokenResponse;
import api.dev.email.EmailService;
import api.dev.exceptions.ResourceNotFoundException;
import api.dev.security.JwtService;
import api.dev.user.dto.JwtResponse;
import api.dev.user.model.JwtToken;
import api.dev.user.model.ResetTokenPassword;
import api.dev.user.repository.JwtTokenRepository;
import api.dev.user.repository.ResetTokenPasswordRepository;
import api.dev.user.repository.UserRepository;
import api.dev.user.repository.VerificationTokenRepository;

@Service
public class AuthenticationService {
    
    private  UserRepository userRepository;
    private  PasswordEncoder passwordEncoder;
    private  AuthenticationManager authenticationManager;
    private  JwtService jwtService;
    private  JwtTokenRepository jwtTokenRepository; 
    private VerificationTokenRepository verificationTokenRepository;
    private EmailService emailService;
    private final ResetTokenPasswordRepository resetTokenPasswordRepository;
    private TwoFactorAuthenticationService twoFactorAuthenticationService;

    


    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JwtService jwtService, JwtTokenRepository jwtTokenRepository,
            VerificationTokenRepository verificationTokenRepository, EmailService emailService,
            ResetTokenPasswordRepository resetTokenPasswordRepository,
            TwoFactorAuthenticationService twoFactorAuthenticationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.jwtTokenRepository = jwtTokenRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.emailService = emailService;
        this.resetTokenPasswordRepository = resetTokenPasswordRepository;
        this.twoFactorAuthenticationService = twoFactorAuthenticationService;
    }


    public ResponseEntity<String> signup(SignupDto dto, String urlOfRequest) {
        
        if(userRepository.findByEmail(dto.getEmail()).isPresent())
            return ResponseEntity.status(409).body("email aleredy exist");

        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        
        User newUser = new User(dto.getFirstname(), dto.getLastname(), dto.getEmail(), encodedPassword, dto.getRole());
        
        userRepository.save(newUser);

        String generateVerificationToken = UUID.randomUUID().toString();
        
        Date currentDate = new Date();

        Date datePlus15Minutes =  getDatePlus15Minutes(currentDate);    

        VerificationToken verificationToken = new VerificationToken(generateVerificationToken, currentDate, datePlus15Minutes, newUser);
        
        verificationTokenRepository.save(verificationToken);

        String url = urlOfRequest + "/api/auth/email-verification?token=" + generateVerificationToken;
        
        emailService.sendValidationEmail(newUser, url);

        return ResponseEntity.ok("Signup successful,  Please, check your email for to complete your registration");
    }


    public ResponseEntity<String> signin(SigninDto dto) {
        JwtResponse jwt = null;
        Authentication authenticatedUser = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getEmail(),dto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
        User user = (User) authenticatedUser.getPrincipal();
        if (user.isMfaEnabled()) 
        {
            user.setSecret(twoFactorAuthenticationService.generateNewSecret());
            userRepository.save(user);
            return ResponseEntity.ok().body(twoFactorAuthenticationService.generateQrCodeImageUri(user.getSecret()));
        }
        
        jwt = jwtService.generateToken(user);
        
        JwtToken jwtToken = new JwtToken(jwt.getJwt(), jwt.getIssuedAt(), jwt.getExpiration(), false ,user);
        
        revokeAllTokenByUser(user.getUserId());
        jwtTokenRepository.save(jwtToken);
        
        String refreshToken = jwtService.generateRefreshToken(user);
        userRepository.save(user);
        return ResponseEntity.ok().body(new TokenResponse(jwt.getJwt() , refreshToken).toString());     
    }

    private void revokeAllTokenByUser(Integer userId) 
    {
        List<JwtToken> jwtTokenOfUser = jwtTokenRepository.findAllValidTokenByUser(userId);
        if(jwtTokenOfUser.isEmpty()) {
            return;
        }

        jwtTokenOfUser.forEach(t-> {
            t.setIs_logged_out(true);
        });

        jwtTokenRepository.saveAll(jwtTokenOfUser);
    }


    public ResponseEntity<String> resendEmail(String email, String urlOfRequest) throws ResourceNotFoundException {
        
        User user =  userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("user not found"));

        if (user.isEnabled()) 
            return ResponseEntity.badRequest().body("This account has already been verified, please, login.");

        Date currentDate = new Date();

        Date datePlus15Minutes =  getDatePlus15Minutes(currentDate);  

        String generateVerificationToken = UUID.randomUUID().toString();
        
        VerificationToken verificationToken = new VerificationToken(generateVerificationToken, currentDate, datePlus15Minutes, user);
        
        verificationTokenRepository.save(verificationToken);

        String url = urlOfRequest + "/api/auth/email-verification?token=" + generateVerificationToken;
        
        emailService.sendValidationEmail(user, url);

        return ResponseEntity.ok("Please, check your email for to complete your registration");
    
    }


    public ResponseEntity<String> passwordReset(String email , String url) throws  ResourceNotFoundException 
    {
        User user =  userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("user not found"));

        if (!user.isEnabled()) 
            return  ResponseEntity.badRequest().body("should verify you acount in first. resend email verification.");    

        String passwordResetToken = UUID.randomUUID().toString();
    
        Date currentDate = new Date();

        Date datePlus15Minutes =  getDatePlus15Minutes(currentDate);  
        
        ResetTokenPassword resetTokenPassword = new ResetTokenPassword(passwordResetToken, currentDate, datePlus15Minutes, user);

        resetTokenPasswordRepository.save(resetTokenPassword);
 
        String link = passwordResetEmailLink(url , passwordResetToken);

        emailService.sendPasswordResetVerificationEmail(user, link);
        
        return ResponseEntity.ok("check your email, Click the link to reset your password");
    }
 

    private String passwordResetEmailLink(String url , String passwordResetToken){

        return url + "/api/auth/reset-password?token=" + passwordResetToken;
    }


    public ResponseEntity<String> resetPassword(ResetPasswordDto dto, String token) throws ResourceNotFoundException {
        
        ResetTokenPassword resetTokenPassword = resetTokenPasswordRepository.findByToken(token).orElseThrow(() -> new ResourceNotFoundException("token not found"));

        Date expiredAt = resetTokenPassword.getExpiresAt();

        if (expiredAt.before(new Date())) 
            return ResponseEntity.badRequest().body("token is expired.");

        User user = resetTokenPassword.getUser();

        if(!passwordEncoder.matches(dto.getOldPassword(), user.getPassword()))
            return ResponseEntity.badRequest().body("Incorrect old passord");

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));

        resetTokenPassword.setUser(user);

        resetTokenPasswordRepository.save(resetTokenPassword);
        resetTokenPasswordRepository.delete(resetTokenPassword);
        return ResponseEntity.ok("Password has been reset successfully");
    
    }
 
    private Date getDatePlus15Minutes(Date currentDate)
    {
        // Create a Calendar instance
        Calendar calendar = Calendar.getInstance();

        // Set the calendar to the current date and time
        calendar.setTime(currentDate);

        // Add 15 minutes to the calendar
        calendar.add(Calendar.MINUTE, 15);

        // Get the date 15 minutes later
        Date datePlus15Minutes = calendar.getTime();
        return datePlus15Minutes;
    }

}
