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
import sit.int202.ecommerce.modules.user.dto.request.UserRegisterRequest;
import sit.int202.ecommerce.modules.user.dto.response.UserRegisterResponse;
import sit.int202.ecommerce.modules.user.service.UserService;

import java.net.URI;

@Tag(name = "User", description = "APIs for user management")
@RestController
@RequestMapping("/v2")
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
    public ResponseEntity<UserRegisterResponse> register(
            @RequestPart(value = "data") @Validated UserRegisterRequest data,

            @Parameter(description = "Front side of ID card")
            @RequestPart(value = "idCardImageFront", required = false) MultipartFile nationalIdFront,

            @Parameter(description = "Back side of ID card")
            @RequestPart(value = "idCardImageBack", required = false) MultipartFile nationalIdBack
    ) {
        UserRegisterResponse response = service.register(data, nationalIdFront, nationalIdBack);
        return ResponseEntity.created(URI.create("/v2/users/" + response.getId())).body(response);
    }

}
