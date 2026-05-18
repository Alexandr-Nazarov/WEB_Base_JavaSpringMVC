package org.example.backend.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Set;

@RestControllerAdvice(annotations = {RestController.class})
public class RestControllerException {

    @ExceptionHandler(exception = ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> error (ConstraintViolationException exception) {
        ValidationErrorResponse errorResponse = new ValidationErrorResponse (
                "Произошла ошибка валидации",
                buildConstraintViolationException(exception.getConstraintViolations()));

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(errorResponse);
    }

    @ExceptionHandler(exception = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> error (MethodArgumentNotValidException exception) {
        ValidationErrorResponse errorResponse = new ValidationErrorResponse (
                "Произошла ошибка валидации",
                buildMethodArgumentNotValidException(exception.getBindingResult()));
                        return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(errorResponse);

    }

    @ExceptionHandler(exception = Exception.class)
    public ResponseEntity<ErrorResponse> error (Exception exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }

    private List<ErrorDescription> buildConstraintViolationException(Set<ConstraintViolation<?>> constraintViolations) {
        return constraintViolations
                .stream()
                .map(res-> new ErrorDescription(res.getPropertyPath().toString(), res.getMessage()))
                .toList();
    }

    private List<ErrorDescription> buildMethodArgumentNotValidException(BindingResult bindingResult){
        return bindingResult
                .getFieldErrors()
                .stream()
                .map(res->new ErrorDescription(res.getField(), res.getDefaultMessage()))
                .toList();
    }
}
