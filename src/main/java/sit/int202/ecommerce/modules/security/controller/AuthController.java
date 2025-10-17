package sit.int202.ecommerce.modules.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sit.int202.ecommerce.modules.security.model.UserPrincipal;
import sit.int202.ecommerce.modules.user.dto.request.ForgotPasswordRequest;
import sit.int202.ecommerce.modules.user.dto.request.ResetPasswordRequest;
import sit.int202.ecommerce.modules.user.dto.request.UserLoginRequest;
import sit.int202.ecommerce.modules.user.dto.request.UserRegisterRequest;
import sit.int202.ecommerce.modules.user.dto.response.TokenResponse;
import sit.int202.ecommerce.modules.security.services.AuthService;
import sit.int202.ecommerce.modules.user.dto.response.TokenValidateResponse;
import sit.int202.ecommerce.modules.user.dto.response.UserResponse;
import sit.int202.ecommerce.modules.user.service.UserService;

import java.net.URI;
import java.util.Map;

@Tag(name = "Auth", description = "APIs for authentication")
@RestController
@RequestMapping("/v2/auth")
@RequiredArgsConstructor
public class AuthController {


    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    @Operation(
            summary = "User login",
            description = "Authenticate user and return access and refresh tokens"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "403", description = "Account not activated")
    })
    public ResponseEntity<TokenResponse> login(@RequestBody @Validated UserLoginRequest request, HttpServletResponse response) {
        Map<String, String> tokens = authService.authenticate(request);

        String accessToken = tokens.get("accessToken");
        String refreshToken = tokens.get("refreshToken");

        TokenResponse tokenResponse = TokenResponse.builder().access_token(accessToken).build();

        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Change to true in production
        cookie.setPath("/");
        response.addCookie(cookie);

        String sameSite = String.format(
                "refresh_token=%s; Path=/; HttpOnly; SameSite=Strict; Secure",
                refreshToken
        );
        response.addHeader("Set-Cookie", sameSite);

        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/register")
    @Operation(
            summary = "User registration",
            description = "Register a new user with optional ID card images"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "409", description = "Email or nickname already exists")
    })
    public ResponseEntity<UserResponse> register(
            @Parameter(description = "User registration data", required = true)
            @RequestPart(value = "data") @Validated UserRegisterRequest data,

            @Parameter(description = "Front side of ID card")
            @RequestPart(value = "idCardImageFront", required = false) MultipartFile idCardImageFront,

            @Parameter(description = "Back side of ID card")
            @RequestPart(value = "idCardImageBack", required = false) MultipartFile idCardImageBack
    ) {
        UserResponse response = authService.register(data, idCardImageFront, idCardImageBack);
        return ResponseEntity.created(URI.create("/auth/users/" + response.getId())).body(response);
    }

    @PostMapping("/verify-email")
    @Operation(
            summary = "Verify email",
            description = "Verify user's email using a JWT token"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email verified successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired token"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponse> verifyEmail(
            @Parameter(description = "JWT token for email verification", required = true)
            @RequestParam("jwtToken") String token
    ) {
        UserResponse response = authService.verifyEmail(token);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserPrincipal userDetails) {
       return ResponseEntity.ok(userService.findById(userDetails.getId()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> tokens = authService.refreshToken(request, response);

        String accessToken = tokens.get("accessToken");
        TokenResponse tokenResponse = TokenResponse.builder().access_token(accessToken).build();

        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("refresh_token", null);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        String sameSite = "refresh_token=null; Path=/; HttpOnly; Max-Age=0; SameSite=Strict; Secure";
        response.addHeader("Set-Cookie", sameSite);


        return ResponseEntity.ok().body("Logged out successfully");
    }

    @PostMapping("/forgot-password")
    @Operation(
            summary = "Forgot password (Request reset link)",
            description = """
        Trigger a password reset process by sending a reset link to the user's email.
        If the email exists in the system, a token will be generated and sent to the user.
        """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reset link has been sent if email exists"),
            @ApiResponse(responseCode = "400", description = "Invalid email format")
    })
    public ResponseEntity<?> forgotPassword(
            @RequestBody @Validated ForgotPasswordRequest request
    ) {
        authService.requestPasswordReset(request.getEmail());
        return ResponseEntity.ok(Map.of(
                "message", "If email exists, a reset link has been sent to your email."
        ));
    }

    @GetMapping("/reset-password/validate")
    @Operation(
            summary = "Validate reset password token",
            description = """
        Validate the reset password token to check if it is valid and not expired.
        Typically used before showing the reset password form on the frontend.
        """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token is valid"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired token")
    })
    public ResponseEntity<?> validateResetToken(
            @RequestParam("token") String token
    ) {
        TokenValidateResponse response = authService.validateResetPasswordToken(token);
        if (!response.isValid()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    @Operation(
            summary = "Reset password",
            description = """
        Reset the user's password using a valid reset token.
        The token must not be expired. This is typically used after the user clicks the reset link sent to their email.
        """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password reset successful"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired token")
    })
    public ResponseEntity<?> resetPassword(
            @RequestBody @Validated ResetPasswordRequest request,
            @RequestParam("token") String token
    ) {
        authService.updatePassword(token, request);
        return ResponseEntity.ok(Map.of(
                "message", "Password reset successful"
        ));
    }

}
