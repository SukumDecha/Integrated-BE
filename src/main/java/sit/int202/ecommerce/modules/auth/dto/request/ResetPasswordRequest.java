package sit.int202.ecommerce.modules.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import sit.int202.ecommerce.modules.user.validation.PasswordPolicy;

@Data
public class ResetPasswordRequest {
    @NotBlank
    @PasswordPolicy
    @Schema(description = "New password of the user", example = "Password_1234")
    private String newPassword;

    @NotBlank
    @PasswordPolicy
    @Schema(description = "Confirm password of the user", example = "Password_1234")
    private String confirmPassword;
}
