
package com.example.demo.controller;

import com.example.demo.domain.Account;
import com.example.demo.service.AccountService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class AccountController {
	
	@Autowired
	private AccountService accountService;
	
	@PostConstruct
	public void helper() {
		accountService.create("v1", "s");
		accountService.create("v2", "s");
		accountService.create("v3", "s");
	}
	
	@GetMapping("/accounts")
	public String list(Model model) {
		
		model.addAttribute("accounts", accountService.list());
		return "accounts";
	}
	
	@GetMapping("/accounts/{accountId}")
	public String get(Model model, @PathVariable Long accountId,
			HttpServletResponse response) {
		
		System.out.println("enter get");
		// error handling https://www.baeldung.com/exception-handling-for-rest-with-spring
		Account account = accountService.findById(accountId).orElseThrow(
			() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such account id: " + accountId)
		);
		
		model.addAttribute("account", account);
		System.out.println("exit get");
		return "account";
	}
	
	@Secured("USER")
	@PostMapping("/accounts/{accountId}/follow")
	public String follow(@PathVariable Long accountId, Principal principal) {
		System.out.println("enter cont.follow");
		Account loggedInAccount = accountService.findByUsername(principal.getName()).orElseThrow(
			() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tried to follow but account with id='" + accountId + "' was not signed in")
		);
		
		accountService.follow(loggedInAccount.getId(), accountId);
		System.out.println("exit cont.follow");
		return "redirect:/accounts/" + accountId;
	}
	
	/*
	@Secured("USER")
	@PostMapping("/accounts/{accountId}/unfollow")
	public String unfollow(@PathVariable Long accountId, Principal principal) {
		System.out.println("enter cont.unfollow");
		Account loggedInAccount = accountService.findByUsername(principal.getName()).orElseThrow(
			() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tried to unfollow but account with id='" + accountId + "' was not signed in")
		);
		
		accountService.follow(loggedInAccount.getId(), accountId);
		System.out.println("exit cont.unfollow");
		return "redirect:/accounts/" + accountId;
	}
	*/
}