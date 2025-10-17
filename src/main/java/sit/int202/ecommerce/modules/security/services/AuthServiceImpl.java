package sit.int202.ecommerce.modules.security.services;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import sit.int202.ecommerce.common.exceptions.AccountNotActivatedException;
import sit.int202.ecommerce.common.exceptions.MissingTokenException;
import sit.int202.ecommerce.modules.security.model.UserPrincipal;
import sit.int202.ecommerce.modules.user.dto.request.ChangePasswordRequest;
import sit.int202.ecommerce.modules.user.dto.request.ResetPasswordRequest;
import sit.int202.ecommerce.modules.user.dto.request.UserLoginRequest;
import sit.int202.ecommerce.modules.user.dto.request.UserRegisterRequest;
import sit.int202.ecommerce.modules.security.jwt.JwtTokenProvider;
import sit.int202.ecommerce.modules.email.service.EmailService;
import sit.int202.ecommerce.modules.user.dto.response.TokenValidateResponse;
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
    private final PasswordEncoder passwordEncoder;

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
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You need to activate your account before signing in.");
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
            if (email == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token or email not found");
            }
        } catch (ExpiredJwtException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token has expired");
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
                if ("refresh_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void updateRefreshTokenCookie(HttpServletResponse response, String newRefreshToken) {
        Cookie cookie = new Cookie("refresh_token", newRefreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Change to true in production
        cookie.setPath("/");
        response.addCookie(cookie);

        String sameSite = String.format(
                "refresh_token=%s; Path=/; HttpOnly; SameSite=Strict; Secure",
                newRefreshToken
        );
        response.addHeader("Set-Cookie", sameSite);

    }

    private boolean isValidEmail(String email) {
        return email != null &&
                !email.isBlank() &&
                email.length() <= 50 &&
                email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private void validatePasswordStrength(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_.])[A-Za-z\\d@$!%*?&_.]{8,}$";
        if (!password.matches(regex)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Password must be at least 8 characters long, contain upper and lower case letters, a number and a special character."
            );
        }
    }

    public String requestPasswordReset(String email) {
        if (!isValidEmail(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email format");
        }

        Optional<UserAccount> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) {
            log.info("Password reset requested for non-existing email: {}", email);
            return "";
        }

        UserAccount user = optionalUser.get();

        if (!user.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account not activated");
        }

        String token = tokenProvider.generateEmailToken(user);

        emailService.sendResetPasswordEmail(user.getEmail(), user.getNickname(), token);
        log.info("Password reset email sent to {}", email);

        return token;
    }

    @Override
    public TokenValidateResponse validateResetPasswordToken(String token) {
        try {
            String email = tokenProvider.getEmailFromToken(token);
            if (email == null) {
                return TokenValidateResponse.builder()
                        .valid(false)
                        .expiresInSeconds(0)
                        .email(null)
                        .build();
            }

            var claims = Jwts.parserBuilder()
                    .setSigningKey(tokenProvider.getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            long expiresInSeconds = (claims.getExpiration().getTime() - System.currentTimeMillis()) / 1000;

            return TokenValidateResponse.builder()
                    .valid(true)
                    .expiresInSeconds(expiresInSeconds)
                    .email(email)
                    .build();
        } catch (Exception e) {
            return TokenValidateResponse.builder()
                    .valid(false)
                    .expiresInSeconds(0)
                    .email(null)
                    .build();
        }
    }

    @Override
    public void updatePassword(String token, ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");
        }

        validatePasswordStrength(request.getNewPassword());

        String email = tokenProvider.getEmailFromToken(token);
        if (email == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }

        userService.updatePasswordByEmail(email, request.getNewPassword());
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New passwords do not match");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAccount user = userService.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid current password");
        }

        validatePasswordStrength(request.getNewPassword());

        userService.updatePasswordByEmail(email, request.getNewPassword());

        log.info("Password changed for user: {}", email);
    }
}