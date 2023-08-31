
package com.example.demo.service;

import com.example.demo.domain.FileObject;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

/*
- FileObjectDto?

- remove image
- add/edit description?
- like/comment image?
*/
@Service
public class AccountFileObjectService {
	
	@Autowired
	private AccountFinderService accountFinderService;
	
	@Autowired
	private FileObjectCreatorService fileObjectCreatorService;
	
	@Autowired
	private FileObjectFinderService fileObjectFinderService;
	
	@Transactional
	public void createImageToAccount(final String username, 
			final MultipartFile file) throws IOException {
		
		fileObjectCreatorService.create(
			accountFinderService.findByUsername(username), file
		);
	}
	
	@Transactional
	public List<FileObject> getAccountsFileObjects(final String username) {
		return fileObjectFinderService.getAccountsFileObjects(
			accountFinderService.findByUsername(username)
		);
	}
}
