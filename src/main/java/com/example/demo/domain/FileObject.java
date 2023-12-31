
package com.example.demo.domain;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.web.multipart.MultipartFile;

@Entity
@AllArgsConstructor @NoArgsConstructor @Data
@ToString(
	exclude = { "content" }
)
public class FileObject extends AbstractPersistable<Long> {
	
	private String name;
    private String mediaType;
    private Long size;
	
	@ManyToOne
	private Account account;
	
	@Lob
	@Basic(fetch = FetchType.LAZY)
	private byte[] content;
	
	private Privacy privacy;
	
	private static final List<String> SUPPORTED_CONTENT_TYPES = Arrays.asList(
		"image/gif", "image/jpeg", "image/png"
	);
	
	public FileObject(final Account account, final Privacy privacy,
			final MultipartFile file, final String mediaType)
			throws IOException {
		
		this(
			file.getName(),
			mediaType,
			file.getSize(),
			account,
			file.getBytes(),
			privacy
		);
	}
	
	public static boolean isSupportedContentType(final String contentType) {
		return SUPPORTED_CONTENT_TYPES.contains(contentType);
	}
}
