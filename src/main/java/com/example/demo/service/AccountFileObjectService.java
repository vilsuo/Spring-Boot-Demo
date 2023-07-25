
package com.example.demo.service;

import com.example.demo.converter.EntityToDtoConverter;
import com.example.demo.datatransfer.RelationDto;
import com.example.demo.domain.Account;
import com.example.demo.domain.FileObject;
import com.example.demo.domain.Status;
import java.util.List;
import java.util.Optional;
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
	private FileObjectService fileObjectService;
	
	@Transactional
	public void createImageToAccount(String username, MultipartFile file) 
			throws IOException {
		
		fileObjectService.create(
			accountFinderService.findByUsername(username), file
		);
	}
	
	@Transactional
	public List<FileObject> getAccountImages(String username) {
		return fileObjectService.getAccountImages(
			accountFinderService.findByUsername(username)
		);
	}
}
