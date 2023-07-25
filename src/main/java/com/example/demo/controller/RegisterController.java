
package com.example.demo.controller;

import com.example.demo.datatransfer.AccountCreationDto;
import com.example.demo.datatransfer.AccountDto;
import com.example.demo.domain.Role;
import com.example.demo.service.AccountCreatorService;
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
	private AccountCreatorService accountCreatorService;
	
	@GetMapping("/register")
	public String home(@ModelAttribute AccountCreationDto accountDto) {
		return "register";
	}
	
	@PostMapping("/register/create")
	public String createAccount(
			@Valid @ModelAttribute AccountCreationDto accountCreationDto,
			BindingResult bindingResult) {
		
		if (bindingResult.hasErrors()) {
			return "register";
		}
		
		Optional<AccountDto> accountOptional
			= accountCreatorService.createAndGetDto(
				accountCreationDto, Role.USER
			);
		
		if (accountOptional.isPresent()) {
			return "login";	
		} else {
			String username = accountCreationDto.getUsername();
			bindingResult.rejectValue(
				"username", null, "Username '" + username + "' is already taken"
			);
			return "register";
		}
	}
}
