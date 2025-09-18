package sit.int202.ecommerce.modules.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sit.int202.ecommerce.modules.security.model.UserPrincipal;
import sit.int202.ecommerce.modules.user.dto.request.UserLoginRequest;
import sit.int202.ecommerce.modules.user.dto.request.UserRegisterRequest;
import sit.int202.ecommerce.modules.user.dto.response.TokenResponse;
import sit.int202.ecommerce.modules.security.services.AuthService;
import sit.int202.ecommerce.modules.user.dto.response.UserResponse;
import sit.int202.ecommerce.modules.user.model.UserAccount;
import sit.int202.ecommerce.modules.user.service.UserService;

import java.net.URI;

@Tag(name = "Auth", description = "APIs for authentication")
@RestController
@RequestMapping("/v2/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/authenticate")
    @Operation(
            summary = "User login",
            description = "Authenticate user and return access and refresh tokens"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "403", description = "Account not activated")
    })
    public ResponseEntity<TokenResponse> login(@RequestBody @Validated UserLoginRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
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


}
