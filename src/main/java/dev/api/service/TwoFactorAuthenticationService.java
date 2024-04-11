package dev.api.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import dev.api.dto.OtpDto;
import dev.api.model.User;
import dev.api.repository.UserRepository;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static dev.samstevens.totp.util.Utils.getDataUriForImage;


@Slf4j
@RequiredArgsConstructor
@Service
public class TwoFactorAuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;



    public String generateNewSecret() {
        return new DefaultSecretGenerator().generate();
    }

    //  The generateQrCodeImageUri() method generates a data URI for the QR code image. This data URI can be used to embed the QR code image directly into a webpage or any other application that supports image display.
    public String generateQrCodeImageUri(String secret) {
        QrData data = new QrData.Builder()
                .label("2FA") //  description for the 2FA setup.
                .secret(secret)
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6) // The number of digits in the generated OTPs.
                .period(60)
                .build();

        QrGenerator generator = new ZxingPngQrGenerator();
        byte[] imageData = new byte[0];
        try {
            imageData = generator.generate(data);
        } catch (QrGenerationException e) {
            log.error("Error while generating QR-CODE");
        }

        return getDataUriForImage(imageData, generator.getImageMimeType());
    }

    public boolean isOtpValid(String secret, String code) 
    {
        TimeProvider timeProvider = new SystemTimeProvider(); // to retrieve the current time. 
        CodeGenerator codeGenerator = new DefaultCodeGenerator(); // generate OTPs based on the provided secret key and the current time.
        CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);//  checking whether a provided OTP is valid for the given secret key.
        return verifier.isValidCode(secret, code); // This method determines whether the OTP is valid by generating an OTP using the secret key and comparing it with the provided OTP.
    }

    public boolean isOtpNotValid(String secret, String code) {
        return !this.isOtpValid(secret, code);
    }

    public String enable2Fa(String username) 
    {
        User user = userRepository.findByUsername(username).orElse(null);
        // if (user == null) {
        //     return "user not found";
        // }
        user.setEnabled(true);
        user.setSecret(this.generateNewSecret());
        userRepository.save(user);
        return this.generateQrCodeImageUri(user.getSecret());
    }

	public String verifyCode(OtpDto dto) 
    {
        User user = userRepository.findByUsername(dto.getUsername()).orElseThrow(() -> new UsernameNotFoundException("user not found"));

        if (this.isOtpNotValid(user.getSecret(), dto.getOtp())) 
        {
            throw new BadCredentialsException("Code is not correct");
        }

        var jwtToken = jwtService.generateToken(user);

        return jwtToken;
    }
}
