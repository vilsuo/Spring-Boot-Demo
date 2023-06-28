
package com.example.demo.error.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
	
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(
			ResourceNotFoundException exception, WebRequest webRequest) {
		
        ErrorDetails error
			= new ErrorDetails(
				LocalDateTime.now(), 
				exception.getMessage(),
                webRequest.getDescription(false)
			);
		
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
	
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ValidationErrorResponse> onConstraintValidationException(
			ConstraintViolationException exception, WebRequest webRequest) {
		
		ValidationErrorResponse error = new ValidationErrorResponse();
		for (ConstraintViolation violation : exception.getConstraintViolations()) {
			error.getViolations().add(
				new Violation(violation.getPropertyPath().toString(), violation.getMessage())
			);
		}
		
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}
}
