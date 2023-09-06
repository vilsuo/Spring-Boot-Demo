
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
	
	/**
	 * 
	 * @param viewer
	 * @param owner
	 * @return
	 * @throws jdk.jshell.spi.ExecutionControl.NotImplementedException
	 * @throws IllegalAccessException 
	 */
	public List<FileObject> viewAccountsFileObjects(
			final Account viewer, final Account owner)
			throws NotImplementedException, IllegalAccessException {
		
		// throw if not allowed to view
		if (!privacyService.isBlockedFromViewingAllResourcesFromAccount(viewer, owner)) {
			throw new IllegalAccessException();
		}
		
		return getFileObjectsAllowedToView(
			viewer, getAccountsFileObjects(owner)
		);
	}
	
	private List<FileObject> getFileObjectsAllowedToView(
			final Account viewer, final List<FileObject> fileObjects) {
		
		return fileObjects
			.stream()
			.filter(fileObject -> {
				try {
					return privacyService.isViewerAllowedToViewFileObject(
						viewer, fileObject
					);
					
				} catch (NotImplementedException ex) {
					throw new RuntimeException(ex);
				}
			}).toList();
	}
	
}
