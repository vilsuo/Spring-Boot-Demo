
package com.example.demo.service;

import com.example.demo.domain.Account;
import com.example.demo.domain.FileObject;
import com.example.demo.service.repository.FileObjectRepository;
import com.example.demo.utility.FileUtility;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/*
TODO
- make create throw descriptive (custom) exception if not supported file format
*/
@Service
public class FileObjectService {
	
	@Autowired
	private FileObjectRepository fileObjectRepository;
	
	public void create(final Account account, final MultipartFile file)
			throws IOException {
		
		if (file == null) {
			throw new IllegalArgumentException(
				"Can not create a FileObject from null MultipartFile"
			);
		}
		
		final String mimeType = FileUtility.getRealMimeType(file);
		
		System.out.println(
			"file: '" + file.getName()
			+ "' has contentType '" + file.getContentType()
			+ "', detected true type: '" + mimeType + "'"
		);
		
		if (!FileObject.isSupportedContentType(mimeType)) {
			throw new IllegalArgumentException(
				"Illegal file content type: " + mimeType
			);
		}
		
		final FileObject fileObject = new FileObject(
			file.getOriginalFilename(),	// name
			mimeType,					// mediatype
			file.getSize(),				// size
			account,					// account
			file.getBytes()				// content
		);
		
		fileObjectRepository.save(fileObject);
	}
	
	public List<FileObject> getAccountImages(final Account account) {
		return fileObjectRepository.findByAccount(account);
	}
}
