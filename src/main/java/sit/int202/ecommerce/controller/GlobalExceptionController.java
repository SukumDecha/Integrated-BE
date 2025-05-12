package sit.int202.ecommerce.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import sit.int202.ecommerce.dto.response.MyErrorResponse;
import sit.int202.ecommerce.exception.BrandNotFoundException;
import sit.int202.ecommerce.exception.SaleItemNotFoundException;


@RestControllerAdvice

public class GlobalExceptionController {

    @ExceptionHandler({
            SaleItemNotFoundException.class,
            BrandNotFoundException.class,
    })
    public ResponseEntity<MyErrorResponse> handleItemNotFound(
            RuntimeException ex, HttpServletRequest request) {
        MyErrorResponse error = MyErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .errorMessage(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<MyErrorResponse> handlePresentExceptions(ValidationException ex, HttpServletRequest request) {
        MyErrorResponse error = MyErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .errorMessage(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MyErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        MyErrorResponse error = MyErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorMessage("Validation errors")
                .path(request.getRequestURI())
                .build();

        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            error.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<MyErrorResponse> handleControllerValidation(
            HandlerMethodValidationException ex, HttpServletRequest request) {
        MyErrorResponse error = MyErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorMessage(ex.getAllErrors().get(0).getDefaultMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(JpaSystemException.class)
    public ResponseEntity<MyErrorResponse> handleJpaSystemException(
            JpaSystemException ex, HttpServletRequest request) {
        MyErrorResponse error = MyErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorMessage("Database error occurred")
                .path(request.getRequestURI())
                .build();

        error.addValidationError("error", ex.getCause().getMessage());

        return ResponseEntity.status(error.getStatus()).body(error);
    }

}
