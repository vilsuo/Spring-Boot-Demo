
package com.example.demo.service;

import com.example.demo.domain.FileObject;
import com.example.demo.domain.Privacy;
import com.example.demo.utility.FileUtility;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

/*
TODO
- remove temporary PLACEHOLDER_PRIVACY value

- is this useless class?

- FileObjectDto?

- remove image
- add/edit description?
- like/comment image?
*/
@Service
public class AccountFileObjectService {
	
	// REMOVE THIS!!
	private final Privacy PLACEHOLDER_PRIVACY = FileUtility.PLACEHOLDER_PRIVACY;
	
	@Autowired
	private AccountFinderService accountFinderService;
	
	@Autowired
	private FileObjectCreatorService fileObjectCreatorService;
	
	@Autowired
	private FileObjectFinderService fileObjectFinderService;
	
	@Transactional
	public void createImageToAccount(final String username, 
			final MultipartFile file) throws IOException {
		
		final Privacy privacy = PLACEHOLDER_PRIVACY;
		
		fileObjectCreatorService.create(
			accountFinderService.findByUsername(username),
			privacy,
			file
		);
	}
	
	@Transactional
	public List<FileObject> getAccountsFileObjects(final String username) {
		return fileObjectFinderService.getAccountsFileObjects(
			accountFinderService.findByUsername(username)
		);
	}
}
