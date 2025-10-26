package sit.int202.ecommerce.modules.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotBlank
    @Schema(description = "Current password", example = "OldPassword_123")
    private String oldPassword;

    @NotBlank
    @Schema(description = "New password", example = "NewPassword_123")
    private String newPassword;

    @NotBlank
    @Schema(description = "Confirm new password", example = "NewPassword_123")
    private String confirmPassword;
}