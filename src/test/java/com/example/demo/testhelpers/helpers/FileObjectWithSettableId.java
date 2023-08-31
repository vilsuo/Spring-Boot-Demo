
package com.example.demo.testhelpers.helpers;

import com.example.demo.domain.Account;
import com.example.demo.domain.FileObject;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public class FileObjectWithSettableId extends FileObject {

	public FileObjectWithSettableId(final Long id, final Account account, 
			final MultipartFile file, final String mediaType) 
			throws IOException {
		
		super(account, file, mediaType);
		super.setId(id);
	}
}
