package sit.int202.ecommerce.modules.security.services;

import org.springframework.web.multipart.MultipartFile;
import sit.int202.ecommerce.modules.user.dto.request.UserLoginRequest;
import sit.int202.ecommerce.modules.user.dto.request.UserRegisterRequest;
import sit.int202.ecommerce.modules.user.dto.response.TokenResponse;
import sit.int202.ecommerce.modules.user.dto.response.UserResponse;

public interface AuthService {
    TokenResponse authenticate(UserLoginRequest request);
    UserResponse register(UserRegisterRequest request, MultipartFile frontImage, MultipartFile backImage);
    UserResponse verifyEmail(String token);
}
