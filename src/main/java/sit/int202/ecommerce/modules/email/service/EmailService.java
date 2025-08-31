package sit.int202.ecommerce.modules.email.service;

public interface EmailService {

    void sendVerificationEmail(String to, String name, String token);

}
