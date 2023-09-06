
package com.example.demo.integration.service;

import com.example.demo.domain.Account;
import com.example.demo.domain.Privacy;
import com.example.demo.domain.Role;
import com.example.demo.domain.Status;
import com.example.demo.service.AccountCreatorService;
import com.example.demo.service.PrivacyService;
import com.example.demo.service.RelationService;
import static com.example.demo.testhelpers.helpers.AccountCreationHelper.validAndUniqueAccountCreationPairForAllRoleCombinationsStream;
import com.example.demo.unit.domain.RoleTest;
import jakarta.transaction.Transactional;
import java.util.stream.Stream;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junitpioneer.jupiter.cartesian.CartesianTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest
public class PrivacyServiceTest {
	
	@Autowired
	private PrivacyService privacyService;
	
	@Autowired
	private AccountCreatorService accountCreatorService;
	
	/*
	@Autowired
	private FileObjectCreatorService fileObjectCreatorService;
	*/
	
	@Autowired
	private RelationService relationService;
	
	//private static List<MockMultipartFile> supportedFiles;
	
	private Stream<Pair<Account, Account>> accountPairStream;
	
	private final Account ANONYMOUS_ACCOUNT = RoleTest.ANONYMOUS_ACCOUNT;
	
	/*
	@BeforeAll
	public static void initFiles() throws IOException, FileNotFoundException {
		supportedFiles = FileObjectCreationHelper.loadSupportedFiles();
	}
	*/
		
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
	public class WithoutRelations {
		
		@CartesianTest
		public void viewersAreNotBlockedFromViewingAllResourcesTest(
				@CartesianTest.Values(booleans = {false, true})
					boolean makeViewerAnonymous) {
			
			accountPairStream.forEach(pair -> {
				final Account viewer = makeViewerAnonymous
					? ANONYMOUS_ACCOUNT
					: pair.getFirst();
				
				final Account target = pair.getFirst();
				try {
					assertFalse(
						privacyService
							.isBlockedFromViewingAllResourcesFromAccount(
								viewer, target
							),
						viewer + " is supposed to not be blocked from viewing "
						+ "all resources of " + target
					);
				} catch (NotImplementedException ex) {
					throw new RuntimeException(ex);
				}
			});
		}
		
		/*
		@ParameterizedTest
		@EnumSource(Privacy.class)
		public void anonymousViewerCanOnlyViewFileObjectWithPrivacyOptionForAll(final Privacy privacy) {
			final Account viewer = ANONYMOUS_ACCOUNT;
			
			accountPairStream.forEach(pair -> {
				final Account owner = pair.getSecond();
				
				for (final MultipartFile file : supportedFiles) {
					try {
						final FileObject fileObject
							= fileObjectCreatorService.create(
								owner, privacy, file
							);
						
						assertEquals(
							privacy == Privacy.ALL,
							privacyService.isViewerAllowedToViewFileObject(
								viewer, fileObject
							)
						);
					} catch (NotImplementedException | IOException ex) {
						throw new RuntimeException(ex);
					}
				}
			});
		}
		*/
		@ParameterizedTest
		@EnumSource(Privacy.class)
		public void anonymousViewerCanOnlyViewResourcesWithPrivacyOptionForAll(
				final Privacy privacy) {
			
			final Account viewer = ANONYMOUS_ACCOUNT;
			accountPairStream.forEach(pair -> {
				final Account owner = pair.getSecond();
				try {
					assertEquals(
						privacy == Privacy.ALL,
						privacyService.isViewerAllowedToViewResource(
							viewer, owner, privacy
						)
					);
				} catch (NotImplementedException ex) {
					throw new RuntimeException(ex);
				}
			});
		}
		
		/*
		@ParameterizedTest
		@EnumSource(Privacy.class)
		public void viewerCanViewTheFileObjectOnlyIfTheViewerIsAdminOrTheFileObjectPrivacyOptionIsForAllTest(final Privacy privacy) {
			accountPairStream.forEach(pair -> {
				final Account viewer = pair.getFirst();
				final Account owner = pair.getSecond();
				
				for (final MultipartFile file : supportedFiles) {
					try {
						final FileObject fileObject
							= fileObjectCreatorService.create(
								owner, privacy, file
							);
						
						assertEquals(
							hasRoleAdmin(viewer) || (privacy == Privacy.ALL),
							privacyService.isViewerAllowedToViewFileObject(
								viewer, fileObject
							)
						);
					} catch (NotImplementedException | IOException ex) {
						throw new RuntimeException(ex);
					}
				}
			});
		}
		*/
		@ParameterizedTest
		@EnumSource(Privacy.class)
		public void viewerCanViewTheResourceOnlyIfTheViewerIsAdminOrTheResourcePrivacyOptionIsForAllTest(
				final Privacy privacy) {
			
			accountPairStream.forEach(pair -> {
				final Account viewer = pair.getFirst();
				final Account owner = pair.getSecond();
				
				try {
					assertEquals(
						hasRoleAdmin(viewer) || (privacy == Privacy.ALL),
						privacyService.isViewerAllowedToViewResource(
							viewer, owner, privacy
						)
					);
				} catch (NotImplementedException ex) {
					throw new RuntimeException(ex);
				}
			});
		}
	}
	
	@Nested
	public class WithRelations {
		
		@CartesianTest
		public void userIsBlockedFromViewingAllResourcesFromAccountOnlyIfBlockExistBetweenThemTest(
				@CartesianTest.Values(booleans = {false, true})
					boolean viewerBlocksOwner,
				@CartesianTest.Values(booleans = {false, true})
					boolean ownerBlocksViewer,
				@CartesianTest.Values(booleans = {false, true})
					boolean mutualFriends) {
			
			accountPairStream.forEach(pair -> {
				final Account viewer = pair.getFirst();
				final Account owner = pair.getSecond();
				
				if (hasRoleUser(viewer)) {
					final boolean blockExists
						= (viewerBlocksOwner || ownerBlocksViewer);
					
					if (viewerBlocksOwner) {
						relationService.create(viewer, owner, Status.BLOCKED);
					}
					
					if (ownerBlocksViewer) {
						relationService.create(owner, viewer, Status.BLOCKED);
					}
					
					if (mutualFriends) {
						relationService.create(viewer, owner, Status.FRIEND);
						relationService.create(owner, viewer, Status.FRIEND);
					}
				
					try {
						assertEquals(
							blockExists,
							privacyService
								.isBlockedFromViewingAllResourcesFromAccount(
									viewer, owner
								),
							getErrorMessage(
								viewer, owner, !blockExists
							)
						);
					} catch (NotImplementedException ex) {
						throw new RuntimeException(ex);
					}
				}
			});
		}
		
		@CartesianTest
		public void adminIsBlockedFromViewingAllResourcesFromAccountOnlyIfViewerHasBlockedOwnerTest(
				@CartesianTest.Values(booleans = {false, true})
					boolean viewerBlocksOwner,
				@CartesianTest.Values(booleans = {false, true})
					boolean ownerBlocksViewer,
				@CartesianTest.Values(booleans = {false, true})
					boolean mutualFriends) {
			
			accountPairStream.forEach(pair -> {
				final Account viewer = pair.getFirst();
				final Account owner = pair.getSecond();
				
				if (hasRoleAdmin(viewer)) {
					if (viewerBlocksOwner) {
						relationService.create(viewer, owner, Status.BLOCKED);
					}
					
					if (ownerBlocksViewer) {
						relationService.create(owner, viewer, Status.BLOCKED);
					}
					
					if (mutualFriends) {
						relationService.create(viewer, owner, Status.FRIEND);
						relationService.create(owner, viewer, Status.FRIEND);
					}
				
					try {
						assertEquals(
							viewerBlocksOwner,
							privacyService
								.isBlockedFromViewingAllResourcesFromAccount(
									viewer, owner
								),
							getErrorMessage(
								viewer, owner, !viewerBlocksOwner
							)
						);
					} catch (NotImplementedException ex) {
						throw new RuntimeException(ex);
					}
				}
			});
		}
		
		/*
		@ParameterizedTest
		@EnumSource(Privacy.class)
		public void adminCanNotViewTheFileObjectIfTheViewerAdminHasBlockedTheFileObjectOwnerTest(final Privacy privacy) {
			accountPairStream.forEach(pair -> {
				final Account viewer = pair.getFirst();
				final Account owner = pair.getSecond();
				
				if (hasRoleAdmin(viewer)) {
					relationService.create(viewer, owner, Status.BLOCKED);
				
					for (final MultipartFile file : supportedFiles) {
						try {
							final FileObject fileObject
								= fileObjectCreatorService.create(
									owner, privacy, file
								);

							assertFalse(
								privacyService.isViewerAllowedToViewFileObject(
									viewer, fileObject
								),
								getErrorMessage(
									viewer, fileObject, false
								)
							);
						} catch (NotImplementedException | IOException ex) {
							throw new RuntimeException(ex);
						}
					}
				}
			});
		}
		*/
		@ParameterizedTest
		@EnumSource(Privacy.class)
		public void adminCanNotViewTheResourceIfTheViewerAdminHasBlockedTheResourceOwnerTest(final Privacy privacy) {
			accountPairStream.forEach(pair -> {
				final Account viewer = pair.getFirst();
				final Account owner = pair.getSecond();
				
				if (hasRoleAdmin(viewer)) {
					relationService.create(viewer, owner, Status.BLOCKED);
				
					try {
						assertFalse(
							privacyService.isViewerAllowedToViewResource(
								viewer, owner, privacy
							),
							getErrorMessage(
								viewer, owner, privacy, false
							)
						);
					} catch (NotImplementedException ex) {
						throw new RuntimeException(ex);
					}
				}
			});
		}
		
		/*
		@ParameterizedTest
		@EnumSource(Privacy.class)
		public void adminCanViewTheFileObjectEvenIfFileObjectOwnerIsAdminAndTheOwnerHasBlockedTheViewerTest(final Privacy privacy) {
			accountPairStream.forEach(pair -> {
				final Account viewer = pair.getFirst();
				final Account owner = pair.getSecond();
				
				if (hasRoleAdmin(viewer) && hasRoleAdmin(owner)) {
					relationService.create(owner, viewer, Status.BLOCKED);
				
					for (final MultipartFile file : supportedFiles) {
						try {
							final FileObject fileObject
								= fileObjectCreatorService.create(
									owner, privacy, file
								);

							assertTrue(
								privacyService.isViewerAllowedToViewFileObject(
									viewer, fileObject
								),
								getErrorMessage(
									viewer, fileObject, true
								)
							);
						} catch (NotImplementedException | IOException ex) {
							throw new RuntimeException(ex);
						}
					}
				}
			});
		}
		*/
		@ParameterizedTest
		@EnumSource(Privacy.class)
		public void adminCanViewTheResourceEvenIfResourceOwnerIsAdminAndTheOwnerHasBlockedTheViewerTest(final Privacy privacy) {
			accountPairStream.forEach(pair -> {
				final Account viewer = pair.getFirst();
				final Account owner = pair.getSecond();
				
				if (hasRoleAdmin(viewer) && hasRoleAdmin(owner)) {
					relationService.create(owner, viewer, Status.BLOCKED);
				
					try {
						assertTrue(
							privacyService.isViewerAllowedToViewResource(
								viewer, owner, privacy
							),
							getErrorMessage(
								viewer, owner, privacy, true
							)
						);
					} catch (NotImplementedException ex) {
						throw new RuntimeException(ex);
					}
				}
			});
		}
		
		/*
		@CartesianTest
		public void userCanNotViewTheFileObjectIfBlockExistsBetweenTheOwnerAndTheViewerTest(
				@CartesianTest.Values(booleans = {false, true}) boolean viewerIsTheOneBlocking,
				@CartesianTest.Enum Privacy privacy) {
			
			accountPairStream.forEach(pair -> {
				final Account viewer = pair.getFirst();
				final Account owner = pair.getSecond();
				
				if (hasRoleUser(viewer)) {
					relationService.create(
						viewerIsTheOneBlocking ? viewer : owner,
						viewerIsTheOneBlocking ? owner : viewer,
						Status.BLOCKED
					);
				
					for (final MultipartFile file : supportedFiles) {
						try {
							final FileObject fileObject
								= fileObjectCreatorService.create(
									owner, privacy, file
								);

							assertFalse(
								privacyService.isViewerAllowedToViewFileObject(
									viewer, fileObject
								),
								getErrorMessage(
									viewer, fileObject, false
								)
							);
						} catch (NotImplementedException | IOException ex) {
							throw new RuntimeException(ex);
						}
					}
				}
			});
		}
		*/
		@CartesianTest
		public void userCanNotViewTheResourceIfBlockExistsBetweenTheOwnerAndTheViewerTest(
				@CartesianTest.Values(booleans = {false, true})
					boolean viewerIsTheOneBlocking,
				@CartesianTest.Enum Privacy privacy) {
			
			accountPairStream.forEach(pair -> {
				final Account viewer = pair.getFirst();
				final Account owner = pair.getSecond();
				
				if (hasRoleUser(viewer)) {
					relationService.create(
						viewerIsTheOneBlocking ? viewer : owner,
						viewerIsTheOneBlocking ? owner : viewer,
						Status.BLOCKED
					);
				
					try {
						assertFalse(
							privacyService.isViewerAllowedToViewResource(
								viewer, owner, privacy
							),
							getErrorMessage(
								viewer, owner, privacy, false
							)
						);
					} catch (NotImplementedException ex) {
						throw new RuntimeException(ex);
					}
				}
			});
		}
		
		/*
		@CartesianTest
		public void userCanViewFileObjectWithPrivacyOptionFriendsOnlyIfTheViewerAndTheOwnerAreMutualFriendsAndNoBlockExistsBetweenThemTest(
				@CartesianTest.Values(booleans = {false, true}) boolean viewerIsFriendOfOwner,
				@CartesianTest.Values(booleans = {false, true}) boolean ownerIsFriendOfViewer,
				@CartesianTest.Values(booleans = {false, true}) boolean viewerBlocksOwner,
				@CartesianTest.Values(booleans = {false, true}) boolean ownerBlocksViewer) {
			
			final Privacy privacyFriends = Privacy.FRIENDS;
			
			accountPairStream.forEach(pair -> {
				final Account viewer = pair.getFirst();
				final Account owner = pair.getSecond();
				
				if (hasRoleUser(viewer)) {
					final boolean relationIsMutual
						= viewerIsFriendOfOwner && ownerIsFriendOfViewer;
					
					if (viewerIsFriendOfOwner) {
						relationService.create(viewer, owner, Status.FRIEND);
					}
					if (ownerIsFriendOfViewer) {
						relationService.create(owner, viewer, Status.FRIEND);
					}
					
					final boolean blockExists
						= (viewerBlocksOwner || ownerBlocksViewer);
					
					if (viewerBlocksOwner) {
						relationService.create(viewer, owner, Status.BLOCKED);
					}
					
					if (ownerBlocksViewer) {
						relationService.create(owner, viewer, Status.BLOCKED);
					}
					
					final boolean shouldBeAllowed
						= (relationIsMutual && !blockExists);
				
					for (final MultipartFile file : supportedFiles) {
						try {
							final FileObject fileObject
								= fileObjectCreatorService.create(
									owner, privacyFriends, file
								);
							
							assertEquals(
								shouldBeAllowed,
								privacyService.isViewerAllowedToViewFileObject(
									viewer, fileObject
								),
								getErrorMessage(
									viewer, fileObject, shouldBeAllowed
								)
							);
						} catch (NotImplementedException | IOException ex) {
							throw new RuntimeException(ex);
						}
					}
				}
			});
		}
		*/
		@CartesianTest
		public void userCanViewResourcesWithPrivacyOptionFriendsOnlyIfTheViewerAndTheOwnerAreMutualFriendsAndNoBlockExistsBetweenThemTest(
				@CartesianTest.Values(booleans = {false, true})
					boolean viewerIsFriendOfOwner,
				@CartesianTest.Values(booleans = {false, true})
					boolean ownerIsFriendOfViewer,
				@CartesianTest.Values(booleans = {false, true})
					boolean viewerBlocksOwner,
				@CartesianTest.Values(booleans = {false, true})
					boolean ownerBlocksViewer) {
			
			final Privacy privacyFriends = Privacy.FRIENDS;
			
			accountPairStream.forEach(pair -> {
				final Account viewer = pair.getFirst();
				final Account owner = pair.getSecond();
				
				if (hasRoleUser(viewer)) {
					final boolean relationIsMutual
						= viewerIsFriendOfOwner && ownerIsFriendOfViewer;
					
					if (viewerIsFriendOfOwner) {
						relationService.create(viewer, owner, Status.FRIEND);
					}
					if (ownerIsFriendOfViewer) {
						relationService.create(owner, viewer, Status.FRIEND);
					}
					
					final boolean blockExists
						= (viewerBlocksOwner || ownerBlocksViewer);
					
					if (viewerBlocksOwner) {
						relationService.create(viewer, owner, Status.BLOCKED);
					}
					
					if (ownerBlocksViewer) {
						relationService.create(owner, viewer, Status.BLOCKED);
					}
					
					final boolean shouldBeAllowed
						= (relationIsMutual && !blockExists);
				
					try {
						assertEquals(
							shouldBeAllowed,
							privacyService.isViewerAllowedToViewResource(
								viewer, owner, privacyFriends
							),
							getErrorMessage(
								viewer, owner, privacyFriends, 
								shouldBeAllowed
							)
						);
					} catch (NotImplementedException  ex) {
						throw new RuntimeException(ex);
					}
				}
			});
		}
		
		/*
		@CartesianTest
		public void userCanViewFileObjectWithPrivacyOptionForAllRegardlessIfFriendStatusExistsUnlessBlockExistsBetweenOwnerAndTheViewerTest(
				@CartesianTest.Values(booleans = {false, true}) boolean viewerIsFriendOfOwner,
				@CartesianTest.Values(booleans = {false, true}) boolean ownerIsFriendOfViewer,
				@CartesianTest.Values(booleans = {false, true}) boolean viewerBlocksOwner,
				@CartesianTest.Values(booleans = {false, true}) boolean ownerBlocksViewer) {
			
			final Privacy privacyAll = Privacy.ALL;
			
			accountPairStream.forEach(pair -> {
				final Account viewer = pair.getFirst();
				final Account owner = pair.getSecond();
				
				if (hasRoleUser(viewer)) {
					if (viewerIsFriendOfOwner) {
						relationService.create(viewer, owner, Status.FRIEND);
					}
					if (ownerIsFriendOfViewer) {
						relationService.create(owner, viewer, Status.FRIEND);
					}
					
					final boolean blockExists
						= (viewerBlocksOwner || ownerBlocksViewer);
					
					if (viewerBlocksOwner) {
						relationService.create(viewer, owner, Status.BLOCKED);
					}
					
					if (ownerBlocksViewer) {
						relationService.create(owner, viewer, Status.BLOCKED);
					}
				
					final boolean shouldBeAllowed = !blockExists;
					for (final MultipartFile file : supportedFiles) {
						try {
							final FileObject fileObject
								= fileObjectCreatorService.create(
									owner, privacyAll, file
								);
							
							assertEquals(
								shouldBeAllowed,
								privacyService.isViewerAllowedToViewFileObject(
									viewer, fileObject
								),
								getErrorMessage(
									viewer, fileObject, shouldBeAllowed
								)
							);
						} catch (NotImplementedException | IOException ex) {
							throw new RuntimeException(ex);
						}
					}
				}
			});
		}
		*/
		@CartesianTest
		public void userCanViewResourcesWithPrivacyOptionForAllRegardlessIfFriendStatusExistsUnlessBlockExistsBetweenOwnerAndTheViewerTest(
				@CartesianTest.Values(booleans = {false, true})
					boolean viewerIsFriendOfOwner,
				@CartesianTest.Values(booleans = {false, true})
					boolean ownerIsFriendOfViewer,
				@CartesianTest.Values(booleans = {false, true})
					boolean viewerBlocksOwner,
				@CartesianTest.Values(booleans = {false, true})
					boolean ownerBlocksViewer) {
			
			final Privacy privacyAll = Privacy.ALL;
			
			accountPairStream.forEach(pair -> {
				final Account viewer = pair.getFirst();
				final Account owner = pair.getSecond();
				
				if (hasRoleUser(viewer)) {
					if (viewerIsFriendOfOwner) {
						relationService.create(viewer, owner, Status.FRIEND);
					}
					if (ownerIsFriendOfViewer) {
						relationService.create(owner, viewer, Status.FRIEND);
					}
					
					final boolean blockExists
						= (viewerBlocksOwner || ownerBlocksViewer);
					
					if (viewerBlocksOwner) {
						relationService.create(viewer, owner, Status.BLOCKED);
					}
					
					if (ownerBlocksViewer) {
						relationService.create(owner, viewer, Status.BLOCKED);
					}
				
					final boolean shouldBeAllowed = !blockExists;
						try {
							assertEquals(
								shouldBeAllowed,
								privacyService.isViewerAllowedToViewResource(
									viewer, owner, privacyAll
								),
								getErrorMessage(
									viewer, owner, privacyAll, shouldBeAllowed
								)
							);
						} catch (NotImplementedException  ex) {
							throw new RuntimeException(ex);
						}
				}
			});
		}
	}
	
	private boolean hasRoleUser(final Account account) {
		return account.getRole() == Role.USER;
	}
	
	private boolean hasRoleAdmin(final Account account) {
		return account.getRole() == Role.ADMIN;
	}
	
	private String getErrorMessage(final Account viewer,
			final Account viewed, final boolean allowed) {
		
		return "Viewer " + viewer + " should " + (allowed ? "" : "not ")
				+ "be blocked from viewing all resources from " + viewed
				+ ", when the Relations " + viewer.getRelationsFrom() + ", and "
				+ viewed.getRelationsFrom()
				+ " have been created";
	}
	
	private String getErrorMessage(
			final Account viewer, final Account resourceOwner, 
			final Privacy resourcePrivacy, final boolean allowed) {
		
		return "Viewer " + viewer + " should " + (allowed ? "" : "not ")
				+ "be able to view a resource with " + resourcePrivacy
				+ " owned by " + resourceOwner + ", when the Relations "
				+ viewer.getRelationsFrom() + ", and "
				+ resourceOwner.getRelationsFrom()
				+ " have been created";
	}
	
	/*
	private String getErrorMessage(
			final Account viewer, final FileObject fileObject, 
			final boolean allowed) {
		
		return "Viewer " + viewer + " should " + (allowed ? "" : "not ")
				+ "be able to view the " + fileObject + ", when the Relations "
				+ viewer.getRelationsFrom() + ", and "
				+ fileObject.getAccount().getRelationsFrom()
				+ " has been created";
	}
	*/
}
