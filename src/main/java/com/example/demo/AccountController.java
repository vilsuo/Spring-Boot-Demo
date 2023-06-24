
package com.example.demo;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class AccountController {
	
	@Autowired
	private AccountService accountService;
	
	@GetMapping("/accounts")
	public String list(Model model, 
			@ModelAttribute(binding = false) AccountDto accountDto) {
		
		model.addAttribute("accounts", accountService.list());
		return "accounts";
	}
	
	// todo
	// - show error message
	@PostMapping("/accounts")
	public String createAccount(
			@Valid @ModelAttribute AccountDto accountDto,
			BindingResult bindingResult) {
		
		if (bindingResult.hasErrors()) {
			System.out.println("unsuccessful");
			System.out.println(bindingResult.getAllErrors().toString());
			return "accounts";
		}
		
		String username = accountDto.getUsername();
		System.out.println("username: " + username);
		
		/* TODO
			- trim username?
				- check database for trimmed username
		*/
		
		if (!accountService.existsByUsername(username)) {
			accountService.create(accountDto);
			System.out.println("successful");
			return "redirect:/accounts";
			
		} else {
			System.out.println("username is taken");
			bindingResult.rejectValue("username", null, "Username '" + username + "' is already taken");
			return "accounts";
		}
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
