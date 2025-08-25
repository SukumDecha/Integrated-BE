package sit.int202.ecommerce.modules.user.dto.response;


import lombok.Data;
import sit.int202.ecommerce.modules.file.dto.FileResponse;
import sit.int202.ecommerce.modules.user.model.UserAccountType;

import java.time.Instant;


@Data
public class UserRegisterResponse {
    private Integer id;
    private String nickname;
    private String email;
    private String fullName;
    private boolean isActive;
    private UserAccountType userType;
}
