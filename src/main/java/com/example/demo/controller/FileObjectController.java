
package com.example.demo.controller;

import com.example.demo.datatransfer.AccountDto;
import com.example.demo.domain.Status;
import com.example.demo.service.AccountService;
import java.io.IOException;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileObjectController {
	
	@Autowired
    private AccountService accountService;
    
    @GetMapping("/accounts/{username}/images")
    public String images(
			Model model, @PathVariable String username, Principal principal) {
		
		AccountDto accountDto = accountService.findDtoByUsername(username);
		
		model.addAttribute("accountDto", accountDto);
		model.addAttribute("totalAccountImages", accountService.getAccountImages(username).size());
		
		if (principal != null) {
			String loggedInUsername = principal.getName();
			model.addAttribute("hasFriend", accountService.hasRelationStatus(loggedInUsername, username, Status.FRIEND));
			model.addAttribute("hasBlock", accountService.hasRelationStatus(loggedInUsername, username, Status.BLOCKED));
		}
		
        return "images";
    }
	
	@Secured("USER")
	@PreAuthorize("#username == authentication.principal.username")
	@PostMapping("accounts/{username}/images/create")
	public String createImage(
			@PathVariable String username,
			@RequestParam("file") MultipartFile file,
			Principal principal) 
			throws IOException, IllegalAccessException {
		
		String loggedInAccountUsername = principal.getName();
		if (!username.equals(loggedInAccountUsername)) {
			throw new IllegalAccessException(
				"Account " + loggedInAccountUsername 
				+ " can not add images to account " + username
			);
		}
		
		accountService.createImageToAccount(principal.getName(), file);

		return "redirect:/accounts/" + username + "/images";
	}
}
