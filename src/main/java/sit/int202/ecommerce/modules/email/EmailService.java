package sit.int202.ecommerce.modules.email;

public interface EmailService {

    void sendVerificationEmail(String to, String name, String token);
    void sendResetPasswordEmail(String to, String name, String token);
}
