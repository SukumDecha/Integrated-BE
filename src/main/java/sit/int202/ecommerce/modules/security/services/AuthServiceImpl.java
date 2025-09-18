package sit.int202.ecommerce.modules.security.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import sit.int202.ecommerce.modules.user.dto.request.UserLoginRequest;
import sit.int202.ecommerce.modules.user.dto.request.UserRegisterRequest;
import sit.int202.ecommerce.modules.security.jwt.JwtTokenProvider;
import sit.int202.ecommerce.modules.email.service.EmailService;
import sit.int202.ecommerce.modules.user.dto.response.TokenResponse;
import sit.int202.ecommerce.modules.user.dto.response.UserResponse;
import sit.int202.ecommerce.modules.user.mapper.UserMapper;
import sit.int202.ecommerce.modules.user.model.UserAccount;
import sit.int202.ecommerce.modules.user.model.UserAccountType;
import sit.int202.ecommerce.modules.user.service.UserService;

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
    public TokenResponse authenticate(UserLoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        if (email == null || email.isBlank() || email.length() > 50 ||
                password == null || password.isBlank() || password.length() > 14 ||
                !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email or password is incorrect.");
        }

        Optional<UserAccount> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email or password is incorrect.");
        }

        UserAccount user = optionalUser.get();

        if (!userService.verifyPassword(user, password)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email or password is incorrect.");
        }

        if (!user.isActive()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You need to activate your account before signing in.");
        }

        String accessToken = tokenProvider.generateAccessToken(user);
        String refreshToken = tokenProvider.generateRefreshToken(user);

        return TokenResponse.builder()
                .access_token(accessToken)
//                .refresh_token(refreshToken)
                .build();
    }

    @Override
    public UserResponse register(UserRegisterRequest request, MultipartFile frontImage, MultipartFile backImage) {
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