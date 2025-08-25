package sit.int202.ecommerce.modules.email.service;

import jakarta.mail.MessagingException;

public interface EmailService {

    void sendVerificationEmail(String to, String name, String token);

}
