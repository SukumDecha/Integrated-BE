package sit.int202.ecommerce.modules.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import sit.int202.ecommerce.modules.auth.dto.LoginRequest;
import sit.int202.ecommerce.modules.auth.dto.RegisterRequest;
import sit.int202.ecommerce.modules.security.jwt.JwtTokenProvider;
import sit.int202.ecommerce.modules.email.service.EmailService;
import sit.int202.ecommerce.modules.user.dto.response.UserResponse;
import sit.int202.ecommerce.modules.user.mapper.UserMapper;
import sit.int202.ecommerce.modules.user.model.UserAccount;
import sit.int202.ecommerce.modules.user.model.UserAccountType;
import sit.int202.ecommerce.modules.user.service.UserService;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final EmailService emailService;
    private final UserService userService;

    private final UserMapper userMapper;

    private final JwtTokenProvider tokenProvider;

    @Override
    public ResponseEntity<?> authenticate(LoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        if (!isValidEmail(email) || password == null || password.isBlank() || password.length() > 14) {
            return ResponseEntity.badRequest().body("Email or password is invalid.");
        }

        Optional<UserAccount> optionalUser = userService.findByEmail(email.trim());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email or Password is incorrect");
        }

        UserAccount user = optionalUser.get();

        if (!userService.verifyPassword(user, password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email or Password is incorrect");
        }

        if (!user.isActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You need to activate your account before signing in.");
        }

        String accessToken = tokenProvider.generateAccessToken(user);
        String refreshToken = tokenProvider.generateRefreshToken(user);

        return ResponseEntity.ok(Map.of(
                "access_token", accessToken,
                "refresh_token", refreshToken
        ));
    }

    @Override
    public UserResponse register(RegisterRequest request, MultipartFile frontImage, MultipartFile backImage) {
        if (userService.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already used");
        }
        if (userService.existsByNickname(request.getNickname())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nickname already used");
        }

        if (request.getUserType() == UserAccountType.SELLER && (frontImage == null || backImage == null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "National ID images are required for sellers");
        }

        UserAccount user = userService.registerUser(request, frontImage, backImage);
        
        String token = tokenProvider.generateEmailToken(user);
        emailService.sendVerificationEmail(user.getEmail(), user.getNickname(), token);

        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse verifyEmail(String token) {
        String email;
        try {
            email = tokenProvider.getEmailFromToken(token);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired token");
        }

        UserAccount user = userService.activateUser(email);
        return userMapper.toUserResponse(user);
    }

    private boolean isValidEmail(String email) {
        return email != null && email.length() <= 50 &&
                email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

}