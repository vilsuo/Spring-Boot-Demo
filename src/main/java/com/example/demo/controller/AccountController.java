
package com.example.demo.controller;

import com.example.demo.domain.Account;
import com.example.demo.service.AccountService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
	
	@GetMapping("/accounts")
	public String list(Model model) {
		
		model.addAttribute("accounts", accountService.list());
		return "accounts";
	}
	
	@GetMapping("/accounts/{accountId}")
	public String get(Model model, @PathVariable Long accountId,
			HttpServletResponse response) {
		
		// error handling https://www.baeldung.com/exception-handling-for-rest-with-spring
		Account account = accountService.findById(accountId).orElseThrow(
			() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such account id: " + accountId)
		);
		
		model.addAttribute("account", account);
		return "account";
	}
	
	@PostMapping("/accounts/{accountId}")
	public String addMessage(Model model, @PathVariable Long accountId,
			String content, HttpServletResponse response) {
		
		// error handling https://www.baeldung.com/exception-handling-for-rest-with-spring
		Account account = accountService.findById(accountId).orElseThrow(
			() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such account id: " + accountId)
		);
		
		model.addAttribute("account", account);
		return "account";
	}
}
