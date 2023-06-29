
package com.example.demo.controller;

import com.example.demo.datatransfer.AccountCreationDto;
import com.example.demo.datatransfer.AccountDto;
import com.example.demo.service.AccountService;
import jakarta.annotation.PostConstruct;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/*
TODO
- error page
- log creation/following/unfollowing...
- custom (error?) messages
- method security

*/
@Controller
public class AccountController {
	
	@Autowired
	private AccountService accountService;
	
	@PostConstruct
	public void helper() {
		accountService.createUSER(new AccountCreationDto("v1", "s"));
		accountService.createUSER(new AccountCreationDto("v2", "s"));
		accountService.createUSER(new AccountCreationDto("v3", "s"));
	}
	
	@GetMapping("/accounts")
	public String list(Model model) {
		
		model.addAttribute("accountDtos", accountService.list());
		return "accounts";
	}
	
	@GetMapping("/accounts/{username}")
	public String get(Model model, @PathVariable String username, Principal principal) {//,
			//HttpServletResponse response) {
		
		AccountDto accountDto = accountService.findDtoByUsername(username);
		model.addAttribute("accountDto", accountDto);
		model.addAttribute("following", accountService.getFollowing(accountDto.getId()));
		model.addAttribute("followers", accountService.getFollowers(accountDto.getId()));
		
		if (principal != null) {
			AccountDto loggedInAccount = accountService.findDtoByUsername(principal.getName());
			model.addAttribute("isfollowing", accountService.isFollowing(loggedInAccount.getId(), accountDto.getId()));
		}
		
		return "account";
	}
	
	@Secured("USER")
	@PostMapping("/accounts/{username}/follow")
	public String follow(@PathVariable("username") String usernameToFollow, Principal principal) {
		
		AccountDto loggedInAccount = accountService.findDtoByUsername(principal.getName());
		AccountDto accountDtoToFollow = accountService.findDtoByUsername(usernameToFollow);
		
		accountService.follow(loggedInAccount.getId(), accountDtoToFollow.getId());
		
		return "redirect:/accounts/" + usernameToFollow;
	}
	
	@Secured("USER")
	@PostMapping("/accounts/{username}/unfollow")
	public String unfollow(@PathVariable("username") String usernameToFollow, Principal principal) {
		
		AccountDto loggedInAccount = accountService.findDtoByUsername(principal.getName());
		AccountDto accountDtoToFollow = accountService.findDtoByUsername(usernameToFollow);
		
		accountService.unfollow(loggedInAccount.getId(), accountDtoToFollow.getId());
		
		return "redirect:/accounts/" + usernameToFollow;
	}
	
	/*
	@PreAuthorize("#username == authentication.principal.username")
	public String (String username) {
		//...
	}
	*/
}
