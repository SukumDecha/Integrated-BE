package sit.int202.ecommerce.modules.security.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import sit.int202.ecommerce.common.exceptions.AccountNotActivatedException;
import sit.int202.ecommerce.common.exceptions.MissingTokenException;
import sit.int202.ecommerce.modules.security.model.UserPrincipal;
import sit.int202.ecommerce.modules.user.dto.request.UserLoginRequest;
import sit.int202.ecommerce.modules.user.dto.request.UserRegisterRequest;
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
    public Map<String, String> authenticate(UserLoginRequest request) {
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

        UserResponse userResponse = userMapper.toUserResponse(user);

        String accessToken = tokenProvider.generateAccessToken(userResponse);
        String refreshToken = tokenProvider.generateRefreshToken(userResponse);

        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        );
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

    @Override
    public UserPrincipal getCurrentUser() {
        return (UserPrincipal) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    @Override
    public Map<String, String> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshToken(request);

        if (refreshToken == null || !tokenProvider.validateRefreshToken(refreshToken)) {
            throw new MissingTokenException("Missing refresh token");
        }

        int userId = tokenProvider.getUserIdFromRefreshToken(refreshToken);
        UserResponse user = userService.findById(userId);

        if (!user.isActive()) {
            throw new AccountNotActivatedException("User account is not activated");
        }

        String newAccessToken = tokenProvider.generateAccessToken(user);
        String newRefreshToken = tokenProvider.generateRefreshToken(user);

        updateRefreshTokenCookie(response, newRefreshToken);
        response.addHeader("Authorization", "Bearer " + newAccessToken);

        return Map.of("accessToken", newAccessToken);
    }

    private String extractRefreshToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void updateRefreshTokenCookie(HttpServletResponse response, String newRefreshToken) {
        Cookie cookie = new Cookie("refreshToken", newRefreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Change to true in production
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    private boolean isValidEmail(String email) {
        return email != null && email.length() <= 50 &&
                email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

}