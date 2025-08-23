package sit.int202.ecommerce.modules.user.dto.response;

import sit.int202.ecommerce.modules.user.model.UserAccountType;

public class UserRegisterResponse {
    private Integer id;
    private String nickname;
    private String email;
    private String fullName;
    private UserAccountType userAccountType;
    private String createdOn;
    private String updatedOn;

    private String bankName;
    private String bankAccountNumber;
    private String mobileNumber;
    private String nationalIdNumber;
    private String nationalIdFrontUrl;
    private String nationalIdBackUrl;

    public UserRegisterResponse() {}
    public UserRegisterResponse(Integer id, String nickname, String email, String fullName,
                                UserAccountType userAccountType, String createdOn, String updatedOn,
                                String bankName, String bankAccountNumber, String mobileNumber,
                                String nationalIdNumber, String nationalIdFrontUrl, String nationalIdBackUrl) {
        this.id = id; this.nickname = nickname; this.email = email; this.fullName = fullName;
        this.userAccountType = userAccountType; this.createdOn = createdOn; this.updatedOn = updatedOn;
        this.bankName = bankName; this.bankAccountNumber = bankAccountNumber; this.mobileNumber = mobileNumber;
        this.nationalIdNumber = nationalIdNumber; this.nationalIdFrontUrl = nationalIdFrontUrl; this.nationalIdBackUrl = nationalIdBackUrl;
    }
    // getters/setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public UserAccountType getAccountType() { return userAccountType; }
    public void setAccountType(UserAccountType userAccountType) { this.userAccountType = userAccountType; }
    public String getCreatedOn() { return createdOn; }
    public void setCreatedOn(String createdOn) { this.createdOn = createdOn; }
    public String getUpdatedOn() { return updatedOn; }
    public void setUpdatedOn(String updatedOn) { this.updatedOn = updatedOn; }
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public String getBankAccountNumber() { return bankAccountNumber; }
    public void setBankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; }
    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
    public String getNationalIdNumber() { return nationalIdNumber; }
    public void setNationalIdNumber(String nationalIdNumber) { this.nationalIdNumber = nationalIdNumber; }
    public String getNationalIdFrontUrl() { return nationalIdFrontUrl; }
    public void setNationalIdFrontUrl(String nationalIdFrontUrl) { this.nationalIdFrontUrl = nationalIdFrontUrl; }
    public String getNationalIdBackUrl() { return nationalIdBackUrl; }
    public void setNationalIdBackUrl(String nationalIdBackUrl) { this.nationalIdBackUrl = nationalIdBackUrl; }
}
