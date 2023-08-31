
package com.example.demo.error.validation;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(value = HttpStatus.UNSUPPORTED_MEDIA_TYPE)
public class IllegalFileContentTypeException extends RuntimeException {
	
	private final String filename;
    private final String contentType;

    public IllegalFileContentTypeException(
			final String filename, final String contentType) {
		
        super(
			"File '" + filename
			+ "' not supported with filetype : '" + contentType + "'"
		);
		
        this.filename = filename;
        this.contentType = contentType;
    }
}
