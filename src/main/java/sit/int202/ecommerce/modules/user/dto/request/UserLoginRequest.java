package sit.int202.ecommerce.modules.user.dto.request;

import lombok.Data;

@Data
public class UserLoginRequest {
    private String email;
    private String password;
}