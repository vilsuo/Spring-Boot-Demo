
package com.example.demo.service;

import com.example.demo.domain.Account;
import com.example.demo.domain.FileObject;
import com.example.demo.service.repository.FileObjectRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
implement more search functionality
*/
@Service
public class FileObjectFinderService {
	
	@Autowired
	private FileObjectRepository fileObjectRepository;
	
	public List<FileObject> getAccountsFileObjects(final Account account) {
		return fileObjectRepository.findByAccount(account);
	}
	
	public List<FileObject> list() {
		return fileObjectRepository.findAll();
	}
}
