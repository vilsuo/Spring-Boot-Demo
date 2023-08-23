
package com.example.demo.integration.service;

import com.codepoetics.protonpack.StreamUtils;
import com.example.demo.domain.Account;
import com.example.demo.domain.FileObject;
import com.example.demo.domain.Role;
import com.example.demo.service.AccountCreatorService;
import com.example.demo.service.FileObjectService;
import static com.example.demo.testhelpers.helpers.AccountCreationHelper.accountCreationDtoForOneOfEachRoleStream;
import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static com.example.demo.testhelpers.helpers.AccountCreationHelper.validAndUniqueAccountCreationDtoStream;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest
public class FileObjectServiceTest {
	
	@Autowired
	private AccountCreatorService accountCreatorService;
	
	@Autowired
	private FileObjectService fileObjectService;
	
	private Stream<Account> accountStream;
	
	// create Account for each role
	@BeforeEach
	public void init() {
		accountStream = accountCreationDtoForOneOfEachRoleStream()
			.map(accountCreationDtoRolePair -> {
				return accountCreatorService.create(
					accountCreationDtoRolePair.getFirst(),
					accountCreationDtoRolePair.getSecond()
				).get();
			});
	}
	
	@Test
	public void creatingFileWithSupportedContentTypeDoesNotThrowTest() {
		accountStream.forEach(account -> {
			for (final String contentType : FileObject.SUPPORTED_CONTENT_TYPES) {
				final MockMultipartFile file 
					= new MockMultipartFile(
					  "file", "hello.txt", 
					  contentType, "Hello, World!".getBytes()
					);
				
				assertDoesNotThrow(
					() -> fileObjectService.create(account, file),
					"Creating a file with supported content type '"
					+ contentType + " throws"
				);
			}
		});
	}
	
	@Test
	public void creatingFileWithUnsupportedContentTypeThrowsTest() {
		final List<String> unsupportedContentTypes = Arrays.asList(
			"application/json", "application/zip", "text/plain"
		);
		
		accountStream.forEach(account -> {
			for (final String contentType : unsupportedContentTypes) {
				final MockMultipartFile file 
					= new MockMultipartFile(
					  "file", "hello.txt", 
					  contentType, "Hello, World!".getBytes()
					);
				
				assertThrows(
					IllegalArgumentException.class,
					() -> fileObjectService.create(account, file),
					"Creating a file with unsupported content type '"
					+ contentType + "' does not throw"
				);
			}
		});
	}
	
	@Test
	public void newAccountsDoNotHaveAnyFilesTest() {
		accountStream.forEach(account -> {
			final List<FileObject> images = fileObjectService
				.getAccountImages(account);
			
			assertTrue(
				images.isEmpty(),
				"New " + account + " has " + images.size() + " file(s)"
			);
		});
	}
}
