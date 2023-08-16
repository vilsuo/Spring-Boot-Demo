
package com.example.demo.service;

import com.example.demo.domain.Account;
import com.example.demo.domain.FileObject;
import com.example.demo.service.repository.FileObjectRepository;
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
			throw new NullPointerException(
				"Can not create a FileObject from null MultipartFile"
			);
		}
		
		final String contentType = file.getContentType();
		if (!isSupportedContentType(contentType)) {
			throw new IllegalArgumentException(
				"Illegal file content type: " + contentType
			);
		}
		
		final FileObject fileObject = new FileObject(
			file.getOriginalFilename(),
			contentType,
			file.getSize(),
			account,
			file.getBytes()
		);
		
		fileObjectRepository.save(fileObject);
	}
	
	private boolean isSupportedContentType(final String contentType) {
		final boolean isGif = "image/gif".equals(contentType);
		final boolean isJpg = "image/jpeg".equals(contentType);
		
		return (isGif || isJpg);
	}
	
	public List<FileObject> getAccountImages(final Account account) {
		return fileObjectRepository.findByAccount(account);
	}
}
