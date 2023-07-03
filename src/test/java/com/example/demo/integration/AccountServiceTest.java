
package com.example.demo.integration;

import com.example.demo.datatransfer.AccountCreationDto;
import com.example.demo.datatransfer.AccountDto;
import com.example.demo.domain.Status;
import com.example.demo.service.AccountService;
import com.example.demo.validator.PasswordValidator;
import com.example.demo.validator.UsernameValidator;
import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/*
TODO WRITE TESTS FOR METHODS:
	- getAccountsRelations
	- getRelationsToAccount
	- createRelationToAccount
	- removeRelationFromAccount

*/
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest
public class AccountServiceTest {

	@Autowired
	private AccountService accountService;
	
	private final String username1 = "valid1";
	private final String username2 = "valid2";
	private final String username3 = "valid3";
	private final String username4 = "valid4";
	
	private final String password = "placeholder";
	
	@Test
	public void credentialsTest() {
		UsernameValidator uValidator = new UsernameValidator();
		assertTrue(uValidator.isValid(username1, null), "Tests are using invalid username: " + username1);
		assertTrue(uValidator.isValid(username2, null), "Tests are using invalid username: " + username2);
		assertTrue(uValidator.isValid(username3, null), "Tests are using invalid username: " + username3);
		assertTrue(uValidator.isValid(username4, null), "Tests are using invalid username: " + username4);
		
		PasswordValidator pValidator = new PasswordValidator();
		assertTrue(pValidator.isValid(password, null), "Tests are using invalid password: " + password);
	}
	
	@BeforeEach
	public void init() {
		accountService.createUSER(new AccountCreationDto(username1, password));
		accountService.createUSER(new AccountCreationDto(username2, password));
		accountService.createUSER(new AccountCreationDto(username3, password));
		accountService.createUSER(new AccountCreationDto(username4, password));
	}
	
	@Test
	public void addRelationTest() {
		assertTrue(accountService.getAccountsRelations(username1).isEmpty());
		assertTrue(accountService.getRelationsToAccount(username1).isEmpty());
		assertTrue(accountService.getAccountsRelations(username2).isEmpty());
		assertTrue(accountService.getRelationsToAccount(username2).isEmpty());
		
		accountService.createRelationToAccount(username1, username2, Status.FRIEND);
		
		assertEquals(1, accountService.getAccountsRelations(username1).size());
		assertTrue(accountService.getRelationsToAccount(username1).isEmpty());
		assertTrue(accountService.getAccountsRelations(username2).isEmpty());
		assertEquals(1, accountService.getRelationsToAccount(username2).size());
	}
	
	
	/*
	@Test
	public void followingDoesNotCauseFollowBackTest() {
		assertFalse(accountService.isFollowing(id1, id2));
		assertFalse(accountService.isFollowing(id2, id1));
		
		accountService.follow(id1, id2);
		
		assertTrue(accountService.isFollowing(id1, id2));
		assertFalse(accountService.isFollowing(id2, id1));
	}
	
	@Test
	public void followingMultipleTimesHasNoEffectTest() {
		assertFalse(accountService.isFollowing(id1, id2));
		assertFalse(accountService.isFollowing(id2, id1));
		
		accountService.follow(id1, id2);
		accountService.follow(id1, id2);
		
		assertEquals(1, accountService.getFollowing(id1).size());
		assertEquals(1, accountService.getFollowers(id2).size());
	}
	
	@Test
	public void unfollowingWhenNotFollowingHasNoEffectTest() {
		accountService.follow(id2, id1);
		accountService.follow(id1, id3);
		
		assertFalse(accountService.isFollowing(id1, id2));
		
		accountService.unfollow(id1, id2);
		
		assertFalse(accountService.isFollowing(id1, id2));
		
		assertTrue(accountService.isFollowing(id2, id1));
		assertTrue(accountService.isFollowing(id1, id3));
	}

	@Test
	public void getFollowersGetFollowingTest() {
		for (Long id : Arrays.asList(id1, id2, id3)) {
			assertTrue(
				accountService.getFollowers(id).isEmpty(),
				"New AccountService with id='" + id + "' has followers"
			);
			assertTrue(
				accountService.getFollowing(id).isEmpty(),
				"New AccountService with id='" + id + "' follows"
			);
		}
		
		accountService.follow(id1, id2);
		assertTrue(accountService.getFollowers(id1).isEmpty());
		assertEquals(1, accountService.getFollowers(id2).size());
		assertTrue(accountService.getFollowers(id2).contains(dto1));
		assertTrue(accountService.getFollowers(id3).isEmpty());
		
		assertEquals(1, accountService.getFollowing(id1).size());
		assertTrue(accountService.getFollowing(id1).contains(dto2));
		assertTrue(accountService.getFollowing(id2).isEmpty());
		assertTrue(accountService.getFollowing(id3).isEmpty());
		
		
		accountService.follow(id1, id3);
		assertTrue(accountService.getFollowers(id1).isEmpty());
		assertEquals(1, accountService.getFollowers(id2).size());
		assertEquals(1, accountService.getFollowers(id3).size());
		assertTrue(accountService.getFollowers(id3).contains(dto1));
		
		assertEquals(2, accountService.getFollowing(id1).size());
		assertTrue(accountService.getFollowing(id1).contains(dto2));
		assertTrue(accountService.getFollowing(id1).contains(dto3));
		assertTrue(accountService.getFollowing(id2).isEmpty());
		assertTrue(accountService.getFollowing(id3).isEmpty());
		
		
		accountService.follow(id2, id1);
		assertEquals(1, accountService.getFollowers(id1).size());
		assertTrue(accountService.getFollowers(id1).contains(dto2));
		assertEquals(1, accountService.getFollowers(id2).size());
		assertEquals(1, accountService.getFollowers(id3).size());
		
		assertEquals(2, accountService.getFollowing(id1).size());
		assertTrue(accountService.getFollowing(id1).contains(dto2));
		assertTrue(accountService.getFollowing(id1).contains(dto3));
		assertEquals(1, accountService.getFollowing(id2).size());
		assertTrue(accountService.getFollowing(id2).contains(dto1));
		assertTrue(accountService.getFollowing(id3).isEmpty());
	}
	
	@Test
	public void isFollowingTest() {
		assertTrue(accountService.getFollowers(id1).isEmpty(), "New AccountService should not have any followers");
		assertTrue(accountService.getFollowing(id1).isEmpty(), "New AccountService should not follow anyone");
		
		assertFalse(accountService.isFollowing(id1, id2), "New AccountService is not supposed to follow anyone");
		assertFalse(accountService.isFollowing(id2, id1), "New AccountService is not supposed to follow anyone");
		
		
		accountService.follow(id1, id2);
		assertTrue(
			accountService.isFollowing(id1, id2),
			"Can not follow"
		);
		assertFalse(
			accountService.isFollowing(id2, id1),
			"After beign followed, the AccountService is not supposed to follow back"
		);
		assertTrue(
			accountService.getFollowers(id1).isEmpty(),
			"Following should not add a follower"
		);
		assertEquals(
			1, accountService.getFollowing(id1).size(),
			"Following should increase the following count"
		);
		assertEquals(
			1, accountService.getFollowers(id2).size(),
			"Being followed by should increase the followers count"
		);
		assertTrue(
			accountService.getFollowing(id2).isEmpty(),
			"Being followed should not increase the following count"
		);
		
		accountService.follow(id2, id1);
		assertTrue(
			accountService.isFollowing(id1, id2),
			"After being followed back, the following should not be removed"
		);
		assertTrue(
			accountService.isFollowing(id2, id1),
			"Can not follow back"
		);
		
		
		accountService.follow(id1, id3);
		assertTrue(
			accountService.isFollowing(id1, id2),
			"After being followed back, the following should not be removed"
		);
		assertEquals(
			1, accountService.getFollowers(id1).size(),
			"Following after being followed should not increase the followers count"
		);
		assertEquals(
			2, accountService.getFollowing(id1).size(),
			"Following a second AccountService should increase the following count"
		);
		
	}
	
	@Test
	public void unFollowTest() {
		List<Long> idList = Arrays.asList(id1, id2, id3, id4);
		for (Long idx : idList) {
			for (Long idy : idList) {
				assertFalse(
					accountService.isFollowing(idx, idy),
					"Initially some account is following other" 
				);
			}
		}
		
		accountService.follow(id1, id2);
		accountService.follow(id1, id3);
		accountService.follow(id2, id3);
		accountService.follow(id2, id1);
		
		assertTrue(accountService.getFollowers(id1).contains(dto2), "Followers list does not contain a follower");
		assertTrue(accountService.getFollowing(id2).contains(dto1), "Following list does not contain a following");
		
		accountService.unfollow(id2, id1);
		
		assertFalse(accountService.getFollowers(id1).contains(dto2), "Followers list still contains a follower who unfollowed");
		assertFalse(accountService.getFollowing(id2).contains(dto1), "Following list still contains a account after unfollowing");
		
		assertFalse(accountService.isFollowing(id2, id1), "AccountService is following even after unfollowing");
		assertTrue(accountService.isFollowing(id2, id3), "Unfollowing caused that account to unfollow other");
		assertTrue(accountService.isFollowing(id1, id2), "Being unfollowed caused to unfollow back");
		assertTrue(accountService.isFollowing(id1, id3), "Being unfollowed caused a transitional unfollow");
		
		accountService.unfollow(id1, id2);
		accountService.unfollow(id1, id3);
		accountService.unfollow(id2, id3);
		
		for (Long idx : idList) {
			for (Long idy : idList) {
				assertFalse(
					accountService.isFollowing(idx, idy),
					"After someone is following someone even tho everyone unfollowed" 
				);
			}
		}
	}
	*/
}

