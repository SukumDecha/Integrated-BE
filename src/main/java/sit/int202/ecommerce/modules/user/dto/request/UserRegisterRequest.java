package sit.int202.ecommerce.modules.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import sit.int202.ecommerce.modules.user.model.UserAccountType;
import sit.int202.ecommerce.modules.user.validation.PasswordPolicy;

@Data
@Schema(description = "Request object for registering a new user")
public class UserRegisterRequest {

    @NotNull
    @Schema(description = "Type of user account", example = "CUSTOMER")
    private UserAccountType userType;

    @NotBlank
    @Schema(description = "User nickname", example = "johnny")
    private String nickname;

    @NotBlank
    @Email
    @Schema(description = "User email address", example = "john@example.com")
    private String email;

    @NotBlank
    @Size(min = 4, max = 40)
    @Schema(description = "Full name of the user", example = "John Doe")
    private String fullname;

    @NotBlank
    @PasswordPolicy
    @Schema(description = "User password (must satisfy password policy)", example = "P@ssw0rd123")
    private String password;

    // Seller-only fields
    @Schema(description = "Mobile number of seller (required if userType is SELLER)", example = "0812345678")
    private String mobileNumber;

    @Schema(description = "Bank account number of seller (required if userType is SELLER)", example = "1234567890")
    private String bankAccountNumber;

    @Schema(description = "Bank name of seller (required if userType is SELLER)", example = "Bangkok Bank")
    private String bankName;

    @Schema(description = "Card ID number of seller (required if userType is SELLER)", example = "1234567890123")
    private String idCardNumber;
}
