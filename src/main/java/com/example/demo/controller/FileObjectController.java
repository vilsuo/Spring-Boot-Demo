
package com.example.demo.controller;

import com.example.demo.datatransfer.AccountDto;
import com.example.demo.domain.Status;
import com.example.demo.service.AccountFileObjectService;
import com.example.demo.service.datatransfer.AccountDtoFinderService;
import com.example.demo.service.AccountRelationService;
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

/*
TODO
- make it so that other users (also anonymous users) can not post images and can
	not see the option to post images

- add Privacy option for post method
*/

@Controller
public class FileObjectController {
	
	@Autowired
    private AccountRelationService accountRelationService;
	
	@Autowired
    private AccountFileObjectService accountFileObjectService;
	
	@Autowired
	private AccountDtoFinderService accountDtoFinderService;
    
    @GetMapping("/accounts/{username}/images")
    public String images(
			Model model, @PathVariable String username, Principal principal) {
		
		final AccountDto accountDto = accountDtoFinderService
			.findByUsername(username);
		
		model.addAttribute("accountDto", accountDto);
		model.addAttribute(
			"totalAccountImages", 
			accountFileObjectService.getAccountsFileObjects(username).size()
		);
		
		if (principal != null) {
			// implement this in a private method?
			final String loggedInUsername = principal.getName();
			model.addAttribute(
				"hasFriend",
				accountRelationService.hasRelationStatus(
					loggedInUsername, username, Status.FRIEND
				)
			);
			model.addAttribute(
				"hasBlock",
				accountRelationService.hasRelationStatus(
					loggedInUsername, username, Status.BLOCKED)
			);
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
		
		final String loggedInAccountUsername = principal.getName();
		if (!username.equals(loggedInAccountUsername)) {
			throw new IllegalAccessException(
				"Account " + loggedInAccountUsername 
				+ " can not add images to account " + username
			);
		}

		accountFileObjectService.createImageToAccount(
			loggedInAccountUsername, file
		);

		return "redirect:/accounts/" + username + "/images";
	}
}
