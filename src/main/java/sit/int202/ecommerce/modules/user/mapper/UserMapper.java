package sit.int202.ecommerce.modules.user.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import sit.int202.ecommerce.modules.user.dto.request.UserRegisterRequest;
import sit.int202.ecommerce.modules.user.dto.response.UserResponse;
import sit.int202.ecommerce.modules.user.model.UserAccount;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final ModelMapper modelMapper;

    public UserResponse toRegisterResponse(UserAccount user) {
        return modelMapper.map(user, UserResponse.class);
    }

    public UserAccount toEntity(UserRegisterRequest request) {
        return modelMapper.map(request, UserAccount.class);
    }

}
