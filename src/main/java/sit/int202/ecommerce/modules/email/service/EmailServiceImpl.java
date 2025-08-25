package sit.int202.ecommerce.modules.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import sit.int202.ecommerce.config.AppProperties;

@Service
public class EmailServiceImpl implements EmailService {

//    private final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final AppProperties appProperties;

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailServiceImpl(AppProperties appProperties, JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.appProperties = appProperties;
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendVerificationEmail(String to, String name, String token){
        try {
            String verificationLink = appProperties.getFrontendUrl() + "/verify-email?token=" + token;

            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("verificationLink", verificationLink);
            context.setVariable("appName", appProperties.getOrganizerEmail());

            String htmlContent = templateEngine.process("account-verification", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Verify your email - SSA-01");
            helper.setText(htmlContent, true);
            helper.setFrom(appProperties.getOrganizerEmail());

            mailSender.send(message);
        } catch (MessagingException e) {
            // Wrap in a runtime exception or log
            throw new RuntimeException("Failed to send verification email", e);
        }
    }
}
