package sit.int202.ecommerce.modules.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import sit.int202.ecommerce.modules.user.model.UserAccountType;
import sit.int202.ecommerce.modules.user.validation.PasswordPolicy;
import sit.int202.ecommerce.modules.user.validation.SellerFieldsRequired;

import jakarta.validation.constraints.*;

@Data
@SellerFieldsRequired
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
    @NotBlank()
    @Pattern(regexp = "\\d*", message = "Bank account number must contain digits only")
    private String mobileNumber;

    @Schema(description = "Bank account number of seller (required if userType is SELLER)", example = "1234567890")
    @Size(max = 50, message = "Bank account number must be at most 50 characters")
    private String bankAccountNumber;

    @Schema(description = "Bank name of seller (required if userType is SELLER)", example = "Bangkok Bank")
    @Size(max = 100, message = "Bank name must be at most 100 characters")
    private String bankName;

    @Schema(description = "Card ID number of seller (required if userType is SELLER)", example = "1234567890123")
    private String idCardNumber;
}