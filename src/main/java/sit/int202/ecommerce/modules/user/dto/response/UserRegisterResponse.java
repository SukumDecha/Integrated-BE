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
    private UserAccountType accountType;
    private Instant createdOn;
    private Instant updatedOn;

    private String bankName;
    private String bankAccountNumber;
    private String mobileNumber;
    private String nationalIdNumber;
    private FileResponse nationalIdFront;
    private FileResponse nationalIdBack;


    public UserRegisterResponse() {}
    public UserRegisterResponse(Integer id, String nickname, String email, String fullName,
                                UserAccountType accountType, Instant createdOn, Instant updatedOn,
                                String bankName, String bankAccountNumber, String mobileNumber,
                                String nationalIdNumber, String nationalIdFrontUrl, String nationalIdBackUrl) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.fullName = fullName;
        this.accountType = accountType;
        this.bankName = bankName;
        this.bankAccountNumber = bankAccountNumber;
        this.mobileNumber = mobileNumber;
        this.nationalIdNumber = nationalIdNumber;
        this.nationalIdFront = nationalIdFront;
        this.nationalIdBack = nationalIdBack;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;

    }

}
