package sit.int202.ecommerce.modules.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequest {

    @NotBlank
    @Schema(description = "User nickname", example = "johnny")
    private String nickname;

    @NotBlank
    @Size(min = 4, max = 40)
    @Schema(description = "Full name of the user", example = "John Doe")
    private String fullname;

}
