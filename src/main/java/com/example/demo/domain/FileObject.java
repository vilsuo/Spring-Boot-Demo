
package com.example.demo.domain;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@AllArgsConstructor @NoArgsConstructor @Data
public class FileObject extends AbstractPersistable<Long> {
	
	private String name;
    private String mediaType;
    private Long size;
	
	@ManyToOne
	private Account account;
	
	@Lob
	@Basic(fetch = FetchType.LAZY)
	private byte[] content;
	
	private static final List<String> SUPPORTED_CONTENT_TYPES = Arrays.asList(
		"image/gif", "image/jpeg", "image/png"
	);
	
	public static boolean isSupportedContentType(final String contentType) {
		return SUPPORTED_CONTENT_TYPES.contains(contentType);
	}
}
