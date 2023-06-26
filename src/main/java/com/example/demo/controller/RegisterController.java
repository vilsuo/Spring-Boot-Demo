
package com.example.demo.controller;

import com.example.demo.datatransfer.AccountDto;
import com.example.demo.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/*
TODO
- log account creation
*/
@Controller
public class RegisterController {
	
	@Autowired
	private AccountService accountService;
	
	@GetMapping("/register")
	public String home(@ModelAttribute AccountDto accountDto) {
		return "register";
	}
	
	@PostMapping("/register/create")
	public String createAccount(
			@Valid @ModelAttribute AccountDto accountDto,
			BindingResult bindingResult) {
		
		if (bindingResult.hasErrors()) {
			System.out.println("unsuccessful");
			System.out.println(bindingResult.getAllErrors().toString());
			return "register";
		}
		
		String username = accountDto.getUsername().trim();
		System.out.println("username: " + username);
		
		if (!accountService.existsByUsername(username)) {
			accountService.create(accountDto);
			System.out.println("created account '" + username + "'");
			return "login";
			
		} else {
			System.out.println("username is taken");
			bindingResult.rejectValue("username", null, "Username '" + username + "' is already taken");
			return "register";
		}
	}
}
