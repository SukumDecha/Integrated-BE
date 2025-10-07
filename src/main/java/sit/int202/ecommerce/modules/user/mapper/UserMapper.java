package sit.int202.ecommerce.modules.user.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import sit.int202.ecommerce.modules.user.dto.request.UserRegisterRequest;
import sit.int202.ecommerce.modules.user.dto.response.SellerResponse;
import sit.int202.ecommerce.modules.user.dto.response.UserResponse;
import sit.int202.ecommerce.modules.user.dto.response.UserSummaryResponse;
import sit.int202.ecommerce.modules.user.model.UserAccount;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final ModelMapper modelMapper;

    public UserResponse toUserResponse(UserAccount user) {
        return modelMapper.map(user, UserResponse.class);
    }

    public UserResponse toSellerResponse(UserAccount user) {
        return modelMapper.map(user, SellerResponse.class);
    }

    public UserAccount toEntity(UserRegisterRequest request) {
        UserAccount user = new UserAccount();

        // แปลง field ทีละตัวเพื่อหลีกเลี่ยงปัญหาการแปลงแบบอัตโนมัติ
        user.setType(request.getUserType());
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setFullname(request.getFullname());
        user.setPassword(request.getPassword());

        // Field สำหรับ Seller (อาจเป็น null สำหรับ user ที่ไม่ใช่ seller)
        user.setMobileNumber(request.getMobileNumber());
        user.setBankAccountNumber(request.getBankAccountNumber());
        user.setBankName(request.getBankName());
        user.setIdCardNumber(request.getIdCardNumber()); // String ไป String - ไม่มีการแปลง

        // ตั้งค่าเริ่มต้น
        user.setActive(false);

        return user;
    }

    public UserSummaryResponse toUserSummaryResponse(UserAccount user) {
        return modelMapper.map(user, UserSummaryResponse.class);
    }
}