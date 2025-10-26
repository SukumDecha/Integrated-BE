package sit.int202.ecommerce.modules.email.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sit.int202.ecommerce.modules.email.service.EmailService;

@RestController
@RequestMapping("/v1/emails")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

//     @PostMapping("/send-verification")
//     public ResponseEntity<Void> sendVerificationEmail() {
//         emailService.sendVerificationEmail("ninemaster12gt@gmail.com", "Teprawin", "asdasd");
//         return ResponseEntity.ok().build();
//     }
}
