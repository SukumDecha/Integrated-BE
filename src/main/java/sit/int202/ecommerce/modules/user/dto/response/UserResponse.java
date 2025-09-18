package sit.int202.ecommerce.modules.user.dto.response;


import lombok.Data;
import sit.int202.ecommerce.modules.user.model.UserAccountType;

@Data
public class UserResponse {
    private Integer id;

    private String nickname;
    private String email;
    private String fullName;
    private boolean isActive;
    private UserAccountType userType;
}
