package sit.int202.ecommerce.modules.user.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenValidateResponse {
    private boolean valid;
    private long expiresInSeconds;
    private String email;
}