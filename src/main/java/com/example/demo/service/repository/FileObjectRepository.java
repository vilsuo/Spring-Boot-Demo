
package com.example.demo.service.repository;

import com.example.demo.domain.Account;
import com.example.demo.domain.FileObject;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileObjectRepository extends JpaRepository<FileObject, Long> {
	List<FileObject> findByAccount(Account account);
	
	//List<FileObject> findByAccountAndMediaType(Account account, String mediaType);
}
