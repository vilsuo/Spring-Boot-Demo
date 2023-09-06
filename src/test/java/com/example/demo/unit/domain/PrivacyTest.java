
package com.example.demo.unit.domain;

import com.example.demo.domain.Privacy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

public class PrivacyTest {
	
	@Test
	public void hasThreePrivacyOptionsTest() {
		assertEquals(Privacy.values().length, 3);
	}
	
	@Test
	public void getNameTest() {
		assertEquals(Privacy.ALL.getName(), "ALL");
		assertEquals(Privacy.FRIENDS.getName(), "FRIENDS");
		assertEquals(Privacy.PRIVATE.getName(), "PRIVATE");
	}
	
	@Test
	public void getPrivacyTest() {
		assertEquals(Privacy.getPrivacy("ALL"), Privacy.ALL);
		assertEquals(Privacy.getPrivacy("FRIENDS"), Privacy.FRIENDS);
		assertEquals(Privacy.getPrivacy("PRIVATE"), Privacy.PRIVATE);
		
		final String invalidName = "NONEXISTENT";
		assertNull(Privacy.getPrivacy(invalidName));
		assertNull(Privacy.getPrivacy(null));
	}

	/*
	@Nested
	public class Anonymous {
		
		@CartesianTest
		public void anonymousCanViewTheResourceOnlyIfResourcePrivacyIsForAllTest(
				@CartesianTest.Enum(Privacy.class) Privacy resourcePrivacy) {

			assertEquals(
				resourcePrivacy == Privacy.ALL,
				Privacy.isAnonymousAllowedToViewResource(resourcePrivacy)
			);
		}
	}
	
	@Nested
	public class User {
		
		@Nested
		public class ResourceOwner {
			
			private final boolean viewerIsTheOwnerOfTheResource = true;
			
			@CartesianTest
			public void userThatIsTheResourceOwnerCanViewTheResourceTest(
					@CartesianTest.Enum Privacy resourcePrivacy,
					@CartesianTest.Values(booleans = {true, false})
						boolean doesBlockExistsBetweenTheViewerAndTheOwner,
					@CartesianTest.Values(booleans = {true, false})
						boolean areTheViewerAndTheOwnerMutualFriends)
					throws NotImplementedException {

				assertTrue(
					Privacy.isUserAllowedToViewResource(
						viewerIsTheOwnerOfTheResource,
						resourcePrivacy,
						doesBlockExistsBetweenTheViewerAndTheOwner,
						areTheViewerAndTheOwnerMutualFriends
					)
				);
			}
		}

		@Nested
		public class NonOwner {
			
			private final boolean viewerIsNotTheOwnerOfTheResource = false;
			
			@Nested
			public class PrivacyAll {
				
				private final Privacy allResourcePrivacy = Privacy.ALL;
				
				@CartesianTest
				public void userThatIsNotTheResourceOwnerCanViewResourcesWithPrivacyOptionAllUnlessBlockExistsBetweenTheViewerAndTheOwnerOfTheResourceTest(
						@CartesianTest.Values(booleans = {true, false})
							boolean doesBlockExistsBetweenTheViewerAndTheOwner,
						@CartesianTest.Values(booleans = {true, false})
							boolean areTheViewerAndTheOwnerMutualFriends)
						throws NotImplementedException {

					assertEquals(
						!doesBlockExistsBetweenTheViewerAndTheOwner,
						Privacy.isUserAllowedToViewResource(
							viewerIsNotTheOwnerOfTheResource,
							allResourcePrivacy,
							doesBlockExistsBetweenTheViewerAndTheOwner,
							areTheViewerAndTheOwnerMutualFriends
						)
					);
				}
			}
			
			@Nested
			public class PrivacyFriends {
				
				private final Privacy friendsResourcePrivacy = Privacy.FRIENDS;
				
				@CartesianTest
				public void a(
						@CartesianTest.Values(booleans = {true, false})
							boolean doesBlockExistsBetweenTheViewerAndTheOwner,
						@CartesianTest.Values(booleans = {true, false})
							boolean areTheViewerAndTheOwnerMutualFriends)
						throws NotImplementedException {

					assertEquals(
						!doesBlockExistsBetweenTheViewerAndTheOwner
						&& areTheViewerAndTheOwnerMutualFriends,
						Privacy.isUserAllowedToViewResource(
							viewerIsNotTheOwnerOfTheResource,
							friendsResourcePrivacy,
							doesBlockExistsBetweenTheViewerAndTheOwner,
							areTheViewerAndTheOwnerMutualFriends
						)
					);
				}
			}

			@Nested
			public class PrivacyPrivate {
				
				private final Privacy privateResourcePrivacy = Privacy.PRIVATE;
				
				@CartesianTest
				public void userThatIsNotTheResourceOwnerCanNotViewPrivateResourcesTest(
						@CartesianTest.Values(booleans = {true, false})
							boolean doesBlockExistsBetweenTheViewerAndTheOwner,
						@CartesianTest.Values(booleans = {true, false})
							boolean areTheViewerAndTheOwnerMutualFriends)
						throws NotImplementedException {

					assertFalse(
						Privacy.isUserAllowedToViewResource(
							viewerIsNotTheOwnerOfTheResource,
							privateResourcePrivacy,
							doesBlockExistsBetweenTheViewerAndTheOwner,
							areTheViewerAndTheOwnerMutualFriends
						)
					);
				}
			}
		}
	}
	
	@Nested
	public class Admin {
		
		@CartesianTest
		public void adminViewerCanViewTheResourceUnlessTheViewerAdminBlockResourceOwnerTest(
				@CartesianTest.Values(booleans = {true, false})
					boolean hasViewerBlockedTheOwnerOfTheResource) {

			assertNotEquals(
				hasViewerBlockedTheOwnerOfTheResource,
				Privacy.isAdminAllowedToViewResource(
					hasViewerBlockedTheOwnerOfTheResource
				)
			);
		}
	}
	*/
	
	/*
	private void assertPermission(
			final boolean allowed,
			final boolean viewerIsLoggedIn, 
			final boolean viewerIsAdmin,
			final boolean viewerIsTheOwnerOfTheResource, 
			final Privacy resourcePrivacy, 
			final boolean blockExistsBetweenViewerAndOwner, 
			final boolean viewerAndOwnerAreMutualFriends) 
			throws NotImplementedException {
		
		assertEquals(
			allowed,
			Privacy.isAllowedToView(
				viewerIsLoggedIn,
				viewerIsAdmin,
				viewerIsTheOwnerOfTheResource,
				resourcePrivacy,
				blockExistsBetweenViewerAndOwner,
				viewerAndOwnerAreMutualFriends
			),
			"Viewing is supposed to " + (allowed ? "" : "not ")
			+ "be allowed with "
			+ getInfo(
				viewerIsLoggedIn,
				viewerIsAdmin,
				viewerIsTheOwnerOfTheResource,
				resourcePrivacy,
				blockExistsBetweenViewerAndOwner,
				viewerAndOwnerAreMutualFriends
			)
		);
	}
	
	private String getInfo(
			final boolean viewerIsLoggedIn, final boolean viewerIsAdmin,
			final boolean viewerIsTheOwnerOfTheResource, 
			final Privacy resourcePrivacy, 
			final boolean blockExistsBetweenViewerAndOwner, 
			final boolean viewerAndOwnerAreMutualFriends) {
		
		return (viewerIsAdmin ? "" : "Non ") + Role.ADMIN + " viewer is "
			+ (viewerIsLoggedIn ? "" : "not ") + "logged in and is "
			+ (viewerIsTheOwnerOfTheResource ? "" : "not ") + "the owner the "
			+ "resource with Privacy option " + resourcePrivacy + ". There are "
			+ (blockExistsBetweenViewerAndOwner ? "" : "no ") + " Relations "
			+ "with Status " + Status.BLOCKED + " between the viewer and the "
			+ "owner of the resource and they do "
			+ (viewerAndOwnerAreMutualFriends ? "" : "not ") + "have a mutual "
			+ "Relation with Status " + Status.FRIEND;
	}
	*/
}
