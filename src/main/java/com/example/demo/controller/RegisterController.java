
package com.example.demo.controller;

import com.example.demo.datatransfer.AccountCreationDto;
import com.example.demo.datatransfer.AccountDto;
import com.example.demo.service.AccountService;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/*
TODO
- map account to account dt
*/
@Controller
public class RegisterController {
	
	@Autowired
	private AccountService accountService;
	
	@GetMapping("/register")
	public String home(@ModelAttribute AccountCreationDto accountDto) {
		return "register";
	}
	
	@PostMapping("/register/create")
	public String createAccount(
			@Valid @ModelAttribute AccountCreationDto accountCreationDto,
			BindingResult bindingResult) {
		
		if (bindingResult.hasErrors()) {
			System.out.println("unsuccessful");
			System.out.println(bindingResult.getAllErrors().toString());
			return "register";
		}
		
		String username = accountCreationDto.getUsername();
		System.out.println("username: " + username);
		
		if (!accountService.existsByUsername(username)) {
			Optional<AccountDto> createdAccount
				= accountService.createUSER(accountCreationDto);
			
			if (createdAccount.isPresent()) {
				System.out.println("Success: Account '" + username + "' was created.");
				return "login";
				
			} else {
				// never should happen
				System.out.println("Error: Account '" + username + "' was not created!");
				bindingResult.rejectValue("username", null, "Error creating account with username '" + username + "'");
				return "register";
			}
			
		} else {
			System.out.println("username is taken");
			bindingResult.rejectValue("username", null, "Username '" + username + "' is already taken");
			return "register";
		}
	}
}
