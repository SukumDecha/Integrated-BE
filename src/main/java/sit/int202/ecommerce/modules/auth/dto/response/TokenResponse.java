package sit.int202.ecommerce.modules.auth.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenResponse {

    private String access_token;
//    private String refresh_token;
}
