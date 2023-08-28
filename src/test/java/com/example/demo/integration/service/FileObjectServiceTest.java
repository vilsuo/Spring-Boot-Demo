
package com.example.demo.integration.service;

import com.example.demo.domain.Account;
import com.example.demo.domain.FileObject;
import com.example.demo.service.AccountCreatorService;
import com.example.demo.service.FileObjectService;
import static com.example.demo.testhelpers.helpers.AccountCreationHelper.accountCreationDtoForOneOfEachRoleStream;
import com.example.demo.testhelpers.helpers.FileHelper;
import jakarta.transaction.Transactional;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/*
TODO
- add more files with supported content types
- initialize unsupported filetypes in the initFiles method

- test with faked supported content types
*/
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest
public class FileObjectServiceTest {
	
	@Autowired
	private AccountCreatorService accountCreatorService;
	
	@Autowired
	private FileObjectService fileObjectService;
	
	private static List<MockMultipartFile> supportedFiles
		= new ArrayList<>();
	
	private static List<MockMultipartFile> unsupportedFiles
		= new ArrayList<>();
	
	private Stream<Account> accountStream;
	
	@BeforeAll
	public static void initFiles() throws IOException, FileNotFoundException {
		supportedFiles = FileHelper.loadSupportedFiles();
		unsupportedFiles = FileHelper.loadUnsupportedFiles();
	}
	
	/**
	 * Create one {@link Account} for each
	 * {@link com.example.demo.domain.Role Role}
	 */
	@BeforeEach
	public void initAccounts() {
		accountStream = accountCreationDtoForOneOfEachRoleStream()
			.map(accountCreationDtoRolePair -> {
				return accountCreatorService.create(
					accountCreationDtoRolePair.getFirst(),
					accountCreationDtoRolePair.getSecond()
				).get();
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
	
	@Test
	public void creatingFileWithSupportedContentTypeDoesNotThrowTest() {
		accountStream.forEach(account -> {
			for (final MockMultipartFile file : supportedFiles) {
				assertDoesNotThrow(
					() -> fileObjectService.create(account, file),
					"Creating a file '" + file.getName() + "' throws"
				);
			}
		});
	}
	
	@Test
	public void creatingFileWithUnsupportedContentTypeThrowsTest() {
		accountStream.forEach(account -> {
			for (final MockMultipartFile file : unsupportedFiles) {
				assertThrows(
					IllegalArgumentException.class,
					() -> fileObjectService.create(account, file),
					"Creating a file '" + file.getName() + "' does not throw"
				);
			}
		});
	}
}
