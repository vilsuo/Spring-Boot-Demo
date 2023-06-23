
package com.example.demo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AccountController {
	
	@Autowired
	private AccountService accountService;
	
	@GetMapping("/accounts")
	public String list(Model model) {
		model.addAttribute("accounts", accountService.list());
		return "accounts";
	}
	
	@PostMapping("/accounts")
	public String createAccount(
			@RequestParam String password,
			@RequestParam String username) {
		
		boolean success = accountService.create(username, password);
		System.out.println("success:" + success);
		return "redirect:/accounts";
	}
}
