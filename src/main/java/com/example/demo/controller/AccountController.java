
package com.example.demo.controller;

import com.example.demo.datatransfer.AccountDto;
import com.example.demo.domain.Status;
import com.example.demo.service.AccountService;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/*
TODO
- error page
- log creation/following/unfollowing...
- custom (error?) messages
- method security

- handle account own page
	- no options to follow/block
	- can post pictures
*/
@Controller
public class AccountController {
	
	@Autowired
	private AccountService accountService;
	
	@GetMapping("/accounts")
	public String list(Model model) {
		model.addAttribute("accountDtos", accountService.list());
		return "accounts";
	}
	
	@GetMapping("/accounts/{username}")
	public String get(
			Model model, @PathVariable String username, Principal principal) {
		
		AccountDto accountDto = accountService.findDtoByUsername(username);
		
		// handle better?
		model.addAttribute("accountDto", accountDto);
		model.addAttribute("accountRelations", accountService.getAccountsRelations(username));
		model.addAttribute("relationsToAccount", accountService.getRelationsToAccount(username));
		
		if (principal != null) {
			String loggedInUsername = principal.getName();
			model.addAttribute("hasFriend", accountService.hasRelationStatus(loggedInUsername, username, Status.FRIEND));
			model.addAttribute("hasBlock", accountService.hasRelationStatus(loggedInUsername, username, Status.BLOCKED));
		}
		return "account";
	}
}
