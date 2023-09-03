
package com.example.demo.integration.service;

import com.example.demo.domain.Account;
import com.example.demo.domain.FileObject;
import com.example.demo.domain.Privacy;
import com.example.demo.domain.Relation;
import com.example.demo.domain.Role;
import com.example.demo.domain.Status;
import com.example.demo.service.AccountCreatorService;
import com.example.demo.service.FileObjectCreatorService;
import com.example.demo.service.PrivacyService;
import com.example.demo.service.RelationService;
import static com.example.demo.testhelpers.helpers.AccountCreationHelper.validAndUniqueAccountCreationPairForAllRoleCombinationsStream;
import com.example.demo.testhelpers.helpers.FileObjectCreationHelper;
import jakarta.transaction.Transactional;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import jdk.jshell.spi.ExecutionControl;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.cartesian.CartesianTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

/*
TODO
- test also without logged in Account (viewer == null)
*/
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest
public class PrivacyServiceTest {
	
	@Autowired
	private PrivacyService privacyService;
	
	@Autowired
	private AccountCreatorService accountCreatorService;
	
	@Autowired
	private FileObjectCreatorService fileObjectCreatorService;
	
	@Autowired
	private RelationService relationService;
	
	private static List<MockMultipartFile> supportedFiles;
	
	private Stream<Pair<Account, Account>> accountPairStream;
	
	@BeforeAll
	public static void initFiles() throws IOException, FileNotFoundException {
		supportedFiles = FileObjectCreationHelper.loadSupportedFiles();
	}
		
	@BeforeEach
	public void initAccountPairs() {
		accountPairStream
			= validAndUniqueAccountCreationPairForAllRoleCombinationsStream()
				.map(pairOfPairs -> {
					final Account first = accountCreatorService
						.create(
							pairOfPairs.getFirst().getFirst(),
							pairOfPairs.getFirst().getSecond()
						).get();

					final Account second = accountCreatorService
						.create(
							pairOfPairs.getSecond().getFirst(),
							pairOfPairs.getSecond().getSecond()
						).get();

					return Pair.of(first, second);
				});
	}
	
	@Nested
	public class NoRelations {
		
		@Test
		public void viewerCanViewTheFileObjectOnlyIfTheViewerIsAdminOrTheFileObjectPrivacyOptionIsForAllTest() {
			accountPairStream.forEach(pair -> {
				final Account viewer = pair.getFirst();
				final Account owner = pair.getSecond();
				
				for (final MultipartFile file : supportedFiles) {
					for (final Privacy privacy : Privacy.values()) {
						try {
							final FileObject fileObject
								= fileObjectCreatorService.create(
									owner, privacy, file
								);
							
							assertEquals(
								isAdmin(viewer) || (privacy == Privacy.ALL),
								privacyService.isAllowedToViewFileObject(
									viewer, fileObject
								)
							);
						} catch (NotImplementedException | IOException ex) {
							throw new RuntimeException(ex);
						}
					}	
				}
			});
		}
	}
	
	@Nested
	public class Relations {
		
		@Test
		public void adminCanNotViewTheFileObjectIfTheViewerAdminHasBlockedTheFileObjectOwnerTest() {
			accountPairStream.forEach(pair -> {
				final Account viewer = pair.getFirst();
				final Account owner = pair.getSecond();
				
				if (isAdmin(viewer)) {
					final Relation relation = relationService
						.create(viewer, owner, Status.BLOCKED)
						.get();
				
					for (final MultipartFile file : supportedFiles) {
						for (final Privacy privacy : Privacy.values()) {
							try {
								final FileObject fileObject
									= fileObjectCreatorService.create(
										owner, privacy, file
									);

								assertFalse(
									privacyService.isAllowedToViewFileObject(
										viewer, fileObject
									),
									getErrorMessage(
										viewer, fileObject, relation, false
									)
								);
							} catch (NotImplementedException | IOException ex) {
								throw new RuntimeException(ex);
							}
						}	
					}
				}
			});
		}
		
		@Test
		public void adminCanViewTheFileObjectEvenIfFileObjectOwnerIsAdminAndTheOwnerHasBlockedTheViewerTest() {
			accountPairStream.forEach(pair -> {
				final Account viewer = pair.getFirst();
				final Account owner = pair.getSecond();
				
				if (isAdmin(viewer) && isAdmin(owner)) {
					final Relation relation = relationService
						.create(owner, viewer, Status.BLOCKED)
						.get();
				
					for (final MultipartFile file : supportedFiles) {
						for (final Privacy privacy : Privacy.values()) {
							try {
								final FileObject fileObject
									= fileObjectCreatorService.create(
										owner, privacy, file
									);

								assertTrue(
									privacyService.isAllowedToViewFileObject(
										viewer, fileObject
									),
									getErrorMessage(
										viewer, fileObject, relation, true
									)
								);
							} catch (NotImplementedException | IOException ex) {
								throw new RuntimeException(ex);
							}
						}	
					}
				}
			});
		}
		
		@CartesianTest
		public void userCanNotViewTheFileObjectIfBlockExistsBetweenTheOwnerAndTheViewerTest(
				@CartesianTest.Values(booleans = {false, true}) boolean viewerIsTheOneBlocking) {
			
			accountPairStream.forEach(pair -> {
				final Account viewer = pair.getFirst();
				final Account owner = pair.getSecond();
				
				if (isUser(viewer)) {
					final Relation relation = relationService.create(
							viewerIsTheOneBlocking ? viewer : owner,
							viewerIsTheOneBlocking ? owner : viewer,
							Status.BLOCKED
						).get();
				
					for (final MultipartFile file : supportedFiles) {
						for (final Privacy privacy : Privacy.values()) {
							try {
								final FileObject fileObject
									= fileObjectCreatorService.create(
										owner, privacy, file
									);

								assertFalse(
									privacyService.isAllowedToViewFileObject(
										viewer, fileObject
									),
									getErrorMessage(
										viewer, fileObject, relation, false
									)
								);
							} catch (NotImplementedException | IOException ex) {
								throw new RuntimeException(ex);
							}
						}	
					}
				}
			});
		}
	}
	
	private boolean isUser(final Account account) {
		return account.getRole() == Role.USER;
	}
	
	private boolean isAdmin(final Account account) {
		return account.getRole() == Role.ADMIN;
	}
	
	private String getErrorMessage(
			final Account viewer, final FileObject fileObject, 
			final Relation relation, final boolean allowed) {
		
		return "Viewer " + viewer + " should " + (allowed ? "" : "not ")
				+ "be able to view the " + fileObject + ", when "
				+ relation + " has been created";
	}
}
