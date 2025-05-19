package sit.int202.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BrandHasSaleItemsException extends RuntimeException {

    public BrandHasSaleItemsException(String message) {
        super(message);
    }

}
