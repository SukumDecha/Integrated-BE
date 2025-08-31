package sit.int202.ecommerce.modules.auth.dto;

import lombok.Data;
import sit.int202.ecommerce.modules.user.model.UserAccountType;
import sit.int202.ecommerce.modules.user.validation.PasswordPolicy;
import sit.int202.ecommerce.modules.user.validation.SellerFieldsRequired;

import jakarta.validation.constraints.*;

@Data
@SellerFieldsRequired
@PasswordPolicy
public class RegisterRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 50, message = "Email must not exceed 50 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 14, message = "Password must be between 8 and 14 characters")
    private String password;

    @NotBlank(message = "Nickname is required")
    @Size(max = 50, message = "Nickname must not exceed 50 characters")
    private String nickname;

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullname;

    @NotNull(message = "User type is required")
    private UserAccountType userType;

    @Size(max = 20, message = "Mobile number must not exceed 20 characters")
    private String mobileNumber;

    @Size(max = 30, message = "Bank account number must not exceed 30 characters")
    private String bankAccountNumber;

    @Size(max = 100, message = "Bank name must not exceed 100 characters")
    private String bankName;

    @Size(max = 20, message = "ID card number must not exceed 20 characters")
    private String idCardNumber;
}