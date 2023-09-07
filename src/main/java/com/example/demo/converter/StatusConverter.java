
package com.example.demo.converter;

import com.example.demo.domain.Status;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

@Component
@Converter(autoApply = true)
public class StatusConverter extends AbstractEnumConverter<Status, String> {
	
	public StatusConverter() {
		super(Status.class);
    }
}
