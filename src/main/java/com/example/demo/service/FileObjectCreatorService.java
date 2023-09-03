
package com.example.demo.service;

import com.example.demo.domain.Account;
import com.example.demo.domain.FileObject;
import com.example.demo.domain.Privacy;
import com.example.demo.error.validation.IllegalFileContentTypeException;
import com.example.demo.service.repository.FileObjectRepository;
import com.example.demo.utility.FileUtility;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/*
TODO
- make created method return Optional?

functionality to implement:
- remove single image
	- by name?
	- by id?
- like system for images
	- one like/account
	- can unlike
*/
@Service
public class FileObjectCreatorService {
	
	@Autowired
	private FileObjectRepository fileObjectRepository;
	
	public FileObject create(final Account account, final Privacy privacy, 
			final MultipartFile file) throws IOException {
		
		if (file == null) {
			throw new IllegalArgumentException(
				"Can not create a FileObject from null MultipartFile"
			);
		}
		
		final String detectedMimeType = FileUtility.getRealMimeType(file);
		
		if (!FileObject.isSupportedContentType(detectedMimeType)) {
			throw new IllegalFileContentTypeException(
				file.getName(), detectedMimeType
			);
		}
		
		final FileObject fileObject = new FileObject(
			account,
			privacy,
			file,
			detectedMimeType
		);
		
		return fileObjectRepository.save(fileObject);
	}
}
