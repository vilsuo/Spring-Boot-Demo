
package com.example.demo.service;

import com.example.demo.domain.Account;
import com.example.demo.domain.FileObject;
import com.example.demo.error.validation.IllegalFileContentTypeException;
import com.example.demo.service.repository.FileObjectRepository;
import com.example.demo.utility.FileUtility;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/*
TODO
- can not add a fileobject with the same name per account?
	- change create return value to Optional?

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
	
	public FileObject create(final Account account, final MultipartFile file)
			throws IOException {
		
		if (file == null) {
			throw new IllegalArgumentException(
				"Can not create a FileObject from null MultipartFile"
			);
		}
		
		final String detectedMimeType = FileUtility.getRealMimeType(file);
		
		/*
		System.out.println(
			"file: '" + file.getName()
			+ "' has contentType '" + file.getContentType()
			+ "', detected true type: '" + mimeType + "'"
		);
		*/
		
		if (!FileObject.isSupportedContentType(detectedMimeType)) {
			throw new IllegalFileContentTypeException(
				file.getName(), detectedMimeType
			);
		}
		/*
		final FileObject fileObject = new FileObject(
			file.getName(),				// name
			mimeType,					// mediatype
			file.getSize(),				// size
			account,					// account
			file.getBytes()				// content
		);
		*/
		
		final FileObject fileObject = new FileObject(
			account, file, detectedMimeType
		);
		
		return fileObjectRepository.save(fileObject);
	}
}
