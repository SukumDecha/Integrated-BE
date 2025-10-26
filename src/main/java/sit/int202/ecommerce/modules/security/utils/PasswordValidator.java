package sit.int202.ecommerce.modules.security.utils;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class PasswordValidator {

    private static final String PASSWORD_REGEX =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_.])[A-Za-z\\d@$!%*?&_.]{8,}$";

    /**
     * Validate the given password against the defined complexity rules.
     * @param password
     * @throws ResponseStatusException if the password does not meet the complexity requirements.
     */
    public void validate(String password) {
        if (!password.matches(PASSWORD_REGEX)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Password must be at least 8 characters long, contain upper and lower case letters, a number, and a special character."
            );
        }
    }
}
