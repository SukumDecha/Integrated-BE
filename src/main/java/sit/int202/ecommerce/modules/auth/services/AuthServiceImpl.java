package sit.int202.ecommerce.modules.auth.services;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
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
import sit.int202.ecommerce.common.utils.CookieUtils;
import sit.int202.ecommerce.common.utils.EmailValidator;
import sit.int202.ecommerce.modules.security.constants.SecurityConstants;
import sit.int202.ecommerce.modules.security.model.UserPrincipal;
import sit.int202.ecommerce.modules.auth.validation.PasswordValidator;
import sit.int202.ecommerce.modules.auth.dto.request.ChangePasswordRequest;
import sit.int202.ecommerce.modules.auth.dto.request.ResetPasswordRequest;
import sit.int202.ecommerce.modules.user.dto.request.UserLoginRequest;
import sit.int202.ecommerce.modules.user.dto.request.UserRegisterRequest;
import sit.int202.ecommerce.modules.security.jwt.JwtTokenProvider;
import sit.int202.ecommerce.modules.email.EmailService;
import sit.int202.ecommerce.modules.auth.dto.response.TokenValidateResponse;
import sit.int202.ecommerce.modules.user.dto.response.UserResponse;
import sit.int202.ecommerce.modules.user.mapper.UserMapper;
import sit.int202.ecommerce.modules.user.model.UserAccount;
import sit.int202.ecommerce.modules.user.model.UserAccountType;
import sit.int202.ecommerce.modules.user.service.UserService;

import java.time.Duration;
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

    private final EmailValidator emailValidator;
    private final PasswordValidator passwordValidator;

    @Override
    public Map<String, String> authenticate(UserLoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        if (emailValidator.isInvalid(email) || passwordValidator.isInvalid(password)) {
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
                SecurityConstants.ACCESS_TOKEN_COOKIE_NAME, accessToken,
                SecurityConstants.REFRESH_TOKEN_COOKIE_NAME, refreshToken
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
        try {
            String email = tokenProvider.getEmailFromToken(token);
            if (email == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token or email not found");
            }

            UserAccount user = userService.activateUser(email);
            return userMapper.toUserResponse(user);
        } catch (ExpiredJwtException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token has expired");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired token");
        }
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
        try {
            String refreshToken = CookieUtils.getCookieValue(request, SecurityConstants.REFRESH_TOKEN_COOKIE_NAME);

            if (refreshToken == null) {
                throw new MissingTokenException("Missing refresh token");
            }

            if (!tokenProvider.validateRefreshToken(refreshToken)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
            }

            int userId = tokenProvider.getUserIdFromRefreshToken(refreshToken);
            UserResponse user = userService.findById(userId);

            if (!user.isActive()) {
                throw new AccountNotActivatedException("User account is not activated");
            }

            String newAccessToken = tokenProvider.generateAccessToken(user);
            String newRefreshToken = tokenProvider.generateRefreshToken(user);

            CookieUtils.setCookie(
                    response,
                    SecurityConstants.REFRESH_TOKEN_COOKIE_NAME,
                    newRefreshToken,
                    Duration.ofDays(1));

            response.addHeader(
                    SecurityConstants.ACCESS_TOKEN_HEADER,
                    SecurityConstants.TOKEN_PREFIX + newAccessToken);

            return Map.of(SecurityConstants.ACCESS_TOKEN_COOKIE_NAME, newAccessToken);
        } catch (ExpiredJwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token has expired");
        }
    }

    @Override
    public String requestPasswordReset(String email) {
        if (emailValidator.isInvalid(email)) {
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

        if (passwordValidator.isInvalid(request.getNewPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Password must be at least 8 characters long, contain upper and lower case letters, a number, and a special character."
            );
        }

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

        if (passwordValidator.isInvalid(request.getNewPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Password must be at least 8 characters long, contain upper and lower case letters, a number, and a special character."
            );
        }

        userService.updatePasswordByEmail(email, request.getNewPassword());

    }
}