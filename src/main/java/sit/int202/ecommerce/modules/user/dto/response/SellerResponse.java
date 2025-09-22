package sit.int202.ecommerce.modules.user.dto.response;


import lombok.Data;

@Data
public class SellerResponse extends UserResponse {

    private String bankName;
    private String bankAccount;
    private String mobileNumber;
}
