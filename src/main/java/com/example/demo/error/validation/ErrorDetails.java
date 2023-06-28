
package com.example.demo.error.validation;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorDetails {
	
    private LocalDateTime timestamp;
    private String message;
    private String details;
	
}