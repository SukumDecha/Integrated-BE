package sit.int202.ecommerce.modules.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sit.int202.ecommerce.modules.user.dto.request.UserLoginRequest;
import sit.int202.ecommerce.modules.user.dto.request.UserRegisterRequest;
import sit.int202.ecommerce.modules.user.dto.response.UserResponse;
import sit.int202.ecommerce.modules.user.service.UserService;

import java.net.URI;

@Tag(name = "User", description = "APIs for user management")
@RestController
@RequestMapping("/v2/users")
public class UserV2Controller {

    private final UserService service;

    public UserV2Controller(UserService service) {
        this.service = service;
    }

    @PostMapping(value = "/register")
    @Operation(
            summary = "Register a user",
            description = "Registers a new user with optional ID card images"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<UserResponse> register(
            @Parameter(description = "User registration data", required = true)
            @RequestPart(value = "data") @Validated UserRegisterRequest data,

            @Parameter(description = "Front side of ID card")
            @RequestPart(value = "idCardImageFront", required = false) MultipartFile nationalIdFront,

            @Parameter(description = "Back side of ID card")
            @RequestPart(value = "idCardImageBack", required = false) MultipartFile nationalIdBack
    ) {
        UserResponse response = service.register(data, nationalIdFront, nationalIdBack);
        return ResponseEntity.created(URI.create("/v2/users/" + response.getId())).body(response);
    }

    @PostMapping(value = "/verify-email")
    @Operation(
            summary = "Verify email",
            description = "Verifies a user's email using a token"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email verified successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired token")
    })
    public ResponseEntity<UserResponse> verifyEmail(
            @RequestParam("jwtToken") String token
    ) {
        UserResponse response = service.verifyEmail(token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/authentications")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        // == Validate Inputs ==
        if (email == null || password == null ||
                email.trim().isEmpty() || password.trim().isEmpty() ||
                email.length() > 50 || password.length() > 14 ||
                !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {

            return ResponseEntity
                    .badRequest()
                    .body("Email or password is invalid.");
        }

        //== Authentication Logic ==
        boolean success = service.verifyLogin(request.getEmail(), request.getPassword());
        if (success) {
            return ResponseEntity.ok().build(); // HTTP 200
        } else {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Email or password is incorrect");
        }
    }


}
