
package com.example.demo.service;

import com.example.demo.domain.Account;
import com.example.demo.domain.FileObject;
import com.example.demo.service.repository.FileObjectRepository;
import java.util.List;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileObjectFinderService {
	
	@Autowired
	private FileObjectRepository fileObjectRepository;
	
	@Autowired
	private PrivacyService privacyService;
	
	public List<FileObject> getAccountsFileObjects(final Account account) {
		return fileObjectRepository.findByAccount(account);
	}
	
	public List<FileObject> list() {
		return fileObjectRepository.findAll();
	}
	
	public List<FileObject> viewAccountsFileObjects(
			final Account viewer, final Account owner) {
		
		return getAccountsFileObjects(owner).stream()
			.filter(fileObject -> {
				try {
					return privacyService
						.isAllowedToViewFileObject(viewer, fileObject);
				} catch (NotImplementedException ex) {
					throw new RuntimeException(ex);
				}
			}).toList();
	}
}
