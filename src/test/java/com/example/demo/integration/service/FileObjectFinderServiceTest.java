
package com.example.demo.integration.service;

import com.example.demo.domain.Account;
import com.example.demo.domain.FileObject;
import com.example.demo.domain.Privacy;
import com.example.demo.error.validation.IllegalFileContentTypeException;
import com.example.demo.service.AccountCreatorService;
import com.example.demo.service.FileObjectCreatorService;
import com.example.demo.service.FileObjectFinderService;
import static com.example.demo.testhelpers.helpers.AccountCreationHelper.accountCreationDtoForOneOfEachRoleStream;
import static com.example.demo.testhelpers.helpers.AccountCreationHelper.validAndUniqueAccountCreationPairForAllRoleCombinationsStream;
import com.example.demo.testhelpers.helpers.FileObjectCreationHelper;
import static com.example.demo.testhelpers.helpers.FileObjectCreationHelper.fileObjectCreateInfo;
import com.example.demo.utility.FileUtility;
import jakarta.transaction.Transactional;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

/*
TODO
- test methods
	- list
	- viewAccountsFileObjects
*/
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest
public class FileObjectFinderServiceTest {
	
	@Autowired
	private FileObjectFinderService fileObjectFinderService;
	
	@Autowired
	private FileObjectCreatorService fileObjectCreatorService;
	
	@Autowired
	private AccountCreatorService accountCreatorService;
	
	private Stream<Account> accountStream;
	
	private static List<MockMultipartFile> supportedFiles;
	private static List<MockMultipartFile> unsupportedFiles;
	
	@BeforeAll
	public static void initFiles() throws IOException, FileNotFoundException {
		supportedFiles = FileObjectCreationHelper.loadSupportedFiles();
		unsupportedFiles = FileObjectCreationHelper.loadUnsupportedFiles();
	}
	
	@Nested
	public class SingleAccounts {
		
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
		public void newAccountsDoNotHaveAnyFileObjectsTest() {
			accountStream.forEach(account -> {
				final List<FileObject> fileObjects = fileObjectFinderService
					.getAccountsFileObjects(account);

				assertTrue(
					fileObjects.isEmpty(),
					"New " + account + " has " + fileObjects.size()
					+ " FileObjects(s)"
				);
			});
		}

		@ParameterizedTest
		@EnumSource(Privacy.class)
		public void creatingFileObjectsWithSupportedContentTypeIncrementsTheAccountsFileObjectListSizeTest(
				final Privacy privacy) {
			
			accountStream.forEach(account -> {
				int createdFileObjects = 0;
				for (final MultipartFile file : supportedFiles) {
					try {
						fileObjectCreatorService
							.create(account, privacy, file);
						
						++createdFileObjects;

					} catch (IOException ex) {
						throw new RuntimeException(ex);
					}

					final int fileObjectsFound = fileObjectFinderService
						.getAccountsFileObjects(account)
						.size();

					assertEquals(
						createdFileObjects, fileObjectsFound,
						account + " has created " + createdFileObjects
						+ " FileObject(s), but only " + fileObjectsFound
						+ " FileObject(s) were found"
					);
				}
			});
		}

		@ParameterizedTest
		@EnumSource(Privacy.class)
		public void creatingFileObjectsWithUnsupportedContentTypeDoesNotIncrementTheAccountsFileObjectListSizeTest(
				final Privacy privacy) {
			
			accountStream.forEach(account -> {
				int fileObjectCreationAttempts = 0;
				for (final MultipartFile file : unsupportedFiles) {
					++fileObjectCreationAttempts;
					assertThrows(
						IllegalFileContentTypeException.class,
						() -> fileObjectCreatorService
							.create(account, privacy, file)
					);

					final int images = fileObjectFinderService
						.getAccountsFileObjects(account)
						.size();

					assertEquals(
						0, images,
						account + " attempt #" + fileObjectCreationAttempts
						+ " with filename '" + file.getName() + "' increased "
						+ "the Accounts image list size"
					);
				}
			});
		}

		@ParameterizedTest
		@EnumSource(Privacy.class)
		public void createdFileObjectsCanBeFoundFromTheAccountsFileObjectListTest(
				final Privacy privacy) {
			
			accountStream.forEach(account -> {
				for (final MultipartFile file : supportedFiles) {
					try {
						final FileObject fileObject = fileObjectCreatorService
							.create(account, privacy, file);

						assertTrue(
							fileObjectFinderService
								.getAccountsFileObjects(account)
								.contains(fileObject),
							fileObjectCreateInfo(account, privacy, file)
							+ " can not be found from the " + account
							+ " FileObject list"
						);

					} catch (IOException ex) {
						throw new RuntimeException(ex);
					}
				}
			});
		}
		
		@ParameterizedTest
		@EnumSource(Privacy.class)
		public void notCreatedFileObjectsCanNotBeFoundFromTheAccountsFileObjectListTest(
				final Privacy privacy) {
			
			accountStream.forEach(account -> {
				for (final MultipartFile file : supportedFiles) {
					try {
						final String mediaType
							= FileUtility.getRealMimeType(file);
						
						final FileObject fileObject
							= new FileObject(
								account,
								privacy,
								file,
								mediaType
							);

						assertFalse(
							fileObjectFinderService
								.getAccountsFileObjects(account)
								.contains(fileObject),
							fileObjectCreateInfo(account, privacy, file)
							+ " can be found from the " + account
							+ " FileObject list"
						);

					} catch (IOException ex) {
						throw new RuntimeException(ex);
					}
				}
			});
		}
	}
	
	@Nested
	public class PairAccounts {
		
		private List<Pair<Account, Account>> accountPairList;
		
		@BeforeEach
		public void initAccountPairs() {
			accountPairList 
				= validAndUniqueAccountCreationPairForAllRoleCombinationsStream()
					.map(pairOfPairs -> {
						final Account account1 = accountCreatorService
							.create(
								pairOfPairs.getFirst().getFirst(),
								pairOfPairs.getFirst().getSecond()
							).get();

						final Account account2 = accountCreatorService
							.create(
								pairOfPairs.getSecond().getFirst(),
								pairOfPairs.getSecond().getSecond()
							).get();

						return Pair.of(account1, account2);
					}).toList();
		}
		
		@ParameterizedTest
		@EnumSource(Privacy.class)
		public void createdFileObjectsCanNotBeFoundFromOtherAccountsFileObjectListTest(
				final Privacy privacy) {
			
			for (final MultipartFile file : supportedFiles) {
				for (final Pair<Account, Account> pair : accountPairList) {
					final Account account1 = pair.getFirst();
					final Account account2 = pair.getSecond();

					try {
						final FileObject fileObject = fileObjectCreatorService
							.create(account1, privacy, file);

						assertFalse(
							fileObjectFinderService
								.getAccountsFileObjects(account2)
								.contains(fileObject),
							"FileObject " + fileObject + " was created to "
							+ account1 + ", but it can be found from "
							+ account2 + " FileObject list"
						);
					} catch (IOException ex) {
						throw new RuntimeException(ex);
					}
				}
			}
		}
	}
}
