package sit.int202.ecommerce.modules.auth.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import sit.int202.ecommerce.modules.auth.dto.LoginRequest;
import sit.int202.ecommerce.modules.auth.dto.RegisterRequest;
import sit.int202.ecommerce.modules.user.dto.response.UserResponse;

public interface AuthService {
    ResponseEntity<?> authenticate(LoginRequest request);
    UserResponse register(RegisterRequest request, MultipartFile frontImage, MultipartFile backImage);
    UserResponse verifyEmail(String token);
}
