package sit.int202.ecommerce.modules.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginRequest {
    @Schema(description = "User email", example = "itbkk.somchai@ad.sit.kmutt.ac.th")
    @NotBlank
    private String email;

    @Schema(description = "User password", example = "itProj24*SOM")
    @NotBlank
    private String password;
}