package sit.int202.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SaleItemNotFoundException extends RuntimeException {
    public SaleItemNotFoundException(String message) {
        super(message);
    }
}

