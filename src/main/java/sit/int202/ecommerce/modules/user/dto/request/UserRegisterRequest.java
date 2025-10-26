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

    @NotNull(message = "User type is required")
    @Schema(description = "Type of user account", example = "CUSTOMER")
    private UserAccountType userType;

    @NotBlank(message = "Nickname is required")
    @Size(min = 3, max = 30, message = "Nickname must be between 3 and 30 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Nickname can only contain letters, numbers, underscores, and hyphens")
    @Schema(description = "User nickname", example = "johnny")
    private String nickname;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Schema(description = "User email address", example = "john@example.com")
    private String email;

    @NotBlank(message = "Full name is required")
    @Size(min = 4, max = 40, message = "Full name must be between 4 and 40 characters")
    @Schema(description = "Full name of the user", example = "John Doe")
    private String fullname;

    @NotBlank(message = "Password is required")
    @PasswordPolicy
    @Schema(description = "User password (must satisfy password policy)", example = "P@ssw0rd123")
    private String password;

    // Seller-only fields (validated by @SellerFieldsRequired class-level annotation)
    @Schema(description = "Mobile number of seller (required if userType is SELLER)", example = "0812345678")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be exactly 10 digits")
    private String mobileNumber;

    @Schema(description = "Bank account number of seller (required if userType is SELLER)", example = "1234567890")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Bank account number must be between 10 and 15 digits")
    private String bankAccountNumber;

    @Schema(description = "Bank name of seller (required if userType is SELLER)", example = "Bangkok Bank")
    @Size(max = 100, message = "Bank name must not exceed 100 characters")
    private String bankName;

    @Schema(description = "Card ID number of seller (required if userType is SELLER)", example = "1234567890123")
    @Pattern(regexp = "^[0-9]{13}$", message = "ID card number must be exactly 13 digits")
    private String idCardNumber;
}