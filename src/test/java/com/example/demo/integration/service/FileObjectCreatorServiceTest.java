
package com.example.demo.integration.service;

import com.example.demo.domain.Account;
import com.example.demo.domain.FileObject;
import com.example.demo.error.validation.IllegalFileContentTypeException;
import com.example.demo.service.AccountCreatorService;
import com.example.demo.service.FileObjectCreatorService;
import static com.example.demo.testhelpers.helpers.AccountCreationHelper.accountCreationDtoForOneOfEachRoleStream;
import com.example.demo.testhelpers.helpers.FileObjectCreationHelper;
import static com.example.demo.testhelpers.helpers.FileObjectCreationHelper.assertFileObjectIsCreatedFromAccountAndMultipartFile;
import static com.example.demo.testhelpers.helpers.FileObjectCreationHelper.fileObjectCreateInfo;
import jakarta.transaction.Transactional;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

/*
TODO
- make created method to return Optional
	- test presentness and not presentness
*/
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest
public class FileObjectCreatorServiceTest {
	
	@Autowired
	private AccountCreatorService accountCreatorService;
	
	@Autowired
	private FileObjectCreatorService fileObjectCreatorService;
	
	private static List<MockMultipartFile> supportedFiles;
	private static List<MockMultipartFile> unsupportedTrueExtensionFiles;
    private static List<MockMultipartFile> unsupportedFakeExtensionFiles;
	
	private Stream<Account> accountStream;
	
	@BeforeAll
	public static void initFiles() throws IOException, FileNotFoundException {
		supportedFiles = FileObjectCreationHelper.loadSupportedFiles();
		
		unsupportedTrueExtensionFiles
			= FileObjectCreationHelper.loadUnsupportedTrueExtensionFiles();
        
        unsupportedFakeExtensionFiles
            = FileObjectCreationHelper.loadUnsupportedFakeExtensionFiles();
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
	public void creatingFileObjectWithNullFileThrowsTest() {
			accountStream.forEach(account -> {
				for (final MultipartFile file : supportedFiles) {
					assertThrows(
						IllegalArgumentException.class,
						() -> fileObjectCreatorService.create(account, null),
						fileObjectCreateInfo(account, file) + " does not throw"
					);
				}
			});
		}

	@Test
	public void creatingFileObjecstWithSupportedContentTypeDoesNotThrowTest() {
			accountStream.forEach(account -> {
				for (final MultipartFile file : supportedFiles) {
					assertDoesNotThrow(
						() -> fileObjectCreatorService.create(account, file),
						fileObjectCreateInfo(account, file) + " throws"
					);
				}
			});
		}

	@Test
	public void creatingFileObjecstWithTrueExtensionAndUnsupportedContentTypeThrowsTest() {
			accountStream.forEach(account -> {
				for (final MultipartFile file : unsupportedTrueExtensionFiles) {
					assertThrows(
						IllegalFileContentTypeException.class,
						() -> fileObjectCreatorService.create(account, file),
						fileObjectCreateInfo(account, file) + " does not throw"
					);
				}
			});
		}

	@Test
	public void creatingFileObjectsWithFakeExtensionAndUnsupportedContentTypeThrowsTest() {
			accountStream.forEach(account -> {
				for (final MultipartFile file : unsupportedFakeExtensionFiles) {
					assertThrows(
						IllegalFileContentTypeException.class,
						() -> fileObjectCreatorService.create(account, file),
						fileObjectCreateInfo(account, file) + " does not throw"
					);
				}
			});
		}

	@Test
	public void createMethodsReturnedValueTakesItsValuesFromTheMethodParametersTest() {
		accountStream.forEach(account -> {
			for (final MultipartFile file : supportedFiles) {
				try {
					final FileObject fileObject = fileObjectCreatorService
						.create(account, file);

					assertFileObjectIsCreatedFromAccountAndMultipartFile(
						fileObject, account, file
					);

				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			}
		});	
	}
	
	// TODO
	/*
	@Test
	public void canCreateTheSameFileTwiceTest() {
		accountStream.forEach(account -> {
			for (final MultipartFile file : supportedFiles) {
				try {
					fileObjectCreatorService
						.create(account, file);

					final FileObject fileObject = fileObjectCreatorService
						.create(account, file);
					
					assertTrue(
						fileObjectCreatorService.getAccountsFileObjects(account)
							.contains(fileObject),
						fileObjectCreateInfo(account, file) + " can not be "
						+ "found from the Accounts FileObject list"
					);

				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			}
		});
	}
	*/
}
