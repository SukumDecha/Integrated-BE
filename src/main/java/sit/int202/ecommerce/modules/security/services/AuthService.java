package sit.int202.ecommerce.modules.security.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import sit.int202.ecommerce.modules.security.model.UserPrincipal;
import sit.int202.ecommerce.modules.user.dto.request.UserLoginRequest;
import sit.int202.ecommerce.modules.user.dto.request.UserRegisterRequest;
import sit.int202.ecommerce.modules.user.dto.response.TokenResponse;
import sit.int202.ecommerce.modules.user.dto.response.UserResponse;

import java.util.Map;

public interface AuthService {
    Map<String, String> authenticate(UserLoginRequest request);

    UserResponse register(UserRegisterRequest request, MultipartFile frontImage, MultipartFile backImage);

    UserResponse verifyEmail(String token);

    UserPrincipal getCurrentUser();

    Map<String, String> refreshToken(HttpServletRequest request, HttpServletResponse response);
}
