package sit.int202.ecommerce.modules.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank
    @Schema(description = "New password of the user", example = "Password_1234")
    private String newPassword;

    @NotBlank
    @Schema(description = "Confirm password of the user", example = "Password_1234")
    private String confirmPassword;
}
