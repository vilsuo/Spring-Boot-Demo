
package com.example.demo.controller;

import com.example.demo.datatransfer.AccountDto;
import com.example.demo.domain.Status;
import com.example.demo.service.AccountWithRelationService;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


/*
TODO
- error page
- log creation/following/unfollowing...
- custom (error?) messages
- method security

*/
@Controller
public class AccountWithRelationController {
	
	@Autowired
	private AccountWithRelationService accountService;
	
	/*
	@PostConstruct
	public void helper() {
		accountService.createUSER(new AccountCreationDto("v1", "s"));
		accountService.createUSER(new AccountCreationDto("v2", "s"));
		accountService.createUSER(new AccountCreationDto("v3", "s"));
	}
	*/
	
	@GetMapping("/accounts")
	public String list(Model model) {
		
		model.addAttribute("accountDtos", accountService.list());
		return "accounts";
	}
	
	@GetMapping("/accounts/{username}")
	public String get(Model model, @PathVariable String username, Principal principal) {
		AccountDto accountDto = accountService.findDtoByUsername(username);
		
		// handle better
		// 
		model.addAttribute("accountDto", accountDto);
		model.addAttribute("accountRelations", accountService.getAccountsRelations(username));
		model.addAttribute("relationsToAccount", accountService.getRelationToAccount(username));
		
		if (principal != null) {
			AccountDto loggedInAccount = accountService.findDtoByUsername(principal.getName());
			//model.addAttribute("isfollowing", accountService.isFollowing(loggedInAccount.getId(), accountDto.getId()));
		}
		
		return "account-with-relation";
	}
	
	@Secured("USER")
	@PostMapping("/accounts/{username}/friend")
	public String addFriend(@PathVariable String username, Principal principal) {
		addRelation(principal.getName(), username, Status.FRIEND);
		
		return "redirect:/accounts/" + username;
	}
	
	@Secured("USER")
	@PostMapping("/accounts/{username}/unfriend")
	public String removeFriend(@PathVariable String username, Principal principal) {
		removeRelation(principal.getName(), username, Status.FRIEND);
		
		return "redirect:/accounts/" + username;
	}
	
	@Secured("USER")
	@PostMapping("/accounts/{username}/block")
	public String addBlocked(@PathVariable("username") String username, Principal principal) {
		addRelation(principal.getName(), username, Status.BLOCKED);
		
		return "redirect:/accounts/" + username;
	}
	
	@Secured("USER")
	@PostMapping("/accounts/{username}/unblock")
	public String removeBlocked(@PathVariable("username") String username, Principal principal) {
		removeRelation(principal.getName(), username, Status.BLOCKED);
		
		return "redirect:/accounts/" + username;
	}
	
	
	private void addRelation(String relationSourceUsername, String relationTargetUsername, Status status) {
		accountService.addRelationToAccount(relationSourceUsername, relationTargetUsername, status);
	}
	
	private void removeRelation(String relationSourceUsername, String relationTargetUsername, Status status) {
		accountService.removeRelationFromAccount(relationSourceUsername, relationTargetUsername, status);
	}
	
	/*
	@PreAuthorize("#username == authentication.principal.username")
	public String (String username) {
		//...
	}
	*/
}
