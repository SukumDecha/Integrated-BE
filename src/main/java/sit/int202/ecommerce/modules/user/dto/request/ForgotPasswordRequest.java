package sit.int202.ecommerce.modules.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequest {

    @NotBlank
    @Email
    @Schema(description = "User email to reset password", example = "user@example.com")
    private String email;
}