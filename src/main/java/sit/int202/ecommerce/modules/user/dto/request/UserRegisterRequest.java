package sit.int202.ecommerce.modules.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import sit.int202.ecommerce.modules.user.model.UserAccountType;
import sit.int202.ecommerce.modules.user.validation.PasswordPolicy;
import sit.int202.ecommerce.modules.user.validation.SellerGroup;

@Data
public class UserRegisterRequest {
    @NotNull
    private UserAccountType userAccountType;

    @NotBlank @Size(min = 4, max = 40)
    private String fullName;

    @NotBlank private String nickname;

    @NotBlank @Email
    private String email;

    @NotBlank @PasswordPolicy
    private String password;

    // seller-only (validateเมื่อใช้ SellerGroup)
    @NotBlank(groups = SellerGroup.class) private String mobileNumber;
    @NotBlank(groups = SellerGroup.class) private String bankAccountNumber;
    @NotBlank(groups = SellerGroup.class) private String bankName;
    @NotBlank(groups = SellerGroup.class) private String nationalIdNumber;


}
