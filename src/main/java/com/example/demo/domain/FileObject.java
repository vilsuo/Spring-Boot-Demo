
package com.example.demo.domain;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
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
}
