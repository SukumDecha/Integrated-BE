package sit.int202.ecommerce.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sit.int202.ecommerce.exception.MyErrorResponse;
import sit.int202.ecommerce.exception.SaleItemNotFoundException;


@RestControllerAdvice

public class GlobalExceptionController {

    @ExceptionHandler(SaleItemNotFoundException.class)
    public ResponseEntity<MyErrorResponse> handleItemNotFound(
            SaleItemNotFoundException ex, HttpServletRequest request) {
        MyErrorResponse error = new MyErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

}
