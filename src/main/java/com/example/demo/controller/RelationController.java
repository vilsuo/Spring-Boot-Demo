
package com.example.demo.controller;

import com.example.demo.domain.Status;
import com.example.demo.service.AccountRelationService;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RelationController {
	
	@Autowired
	private AccountRelationService accountRelationService;
	
	@Secured("USER")
	@PostMapping("/accounts/{username}/friend")
	public String addFriend(@PathVariable String username, Principal principal) {
		addRelation(principal.getName(), username, Status.FRIEND);
		return "redirect:/accounts/" + username;
	}
	
	@Secured("USER")
	@PostMapping("/accounts/{username}/unfriend")
	public String removeFriend(@PathVariable String username, Principal principal) {
		removeRelation(principal.getName(), username, Status.FRIEND);
		return "redirect:/accounts/" + username;
	}
	
	@Secured("USER")
	@PostMapping("/accounts/{username}/block")
	public String addBlocked(@PathVariable String username, Principal principal) {
		addRelation(principal.getName(), username, Status.BLOCKED);
		return "redirect:/accounts/" + username;
	}
	
	@Secured("USER")
	@PostMapping("/accounts/{username}/unblock")
	public String removeBlocked(@PathVariable String username, Principal principal) {
		removeRelation(principal.getName(), username, Status.BLOCKED);
		return "redirect:/accounts/" + username;
	}
	
	private void addRelation(String relationSourceUsername, String relationTargetUsername, Status status) {
		accountRelationService.createRelationToAccount(relationSourceUsername, relationTargetUsername, status);
	}
	
	private void removeRelation(String relationSourceUsername, String relationTargetUsername, Status status) {
		accountRelationService.removeRelationFromAccount(relationSourceUsername, relationTargetUsername, status);
	}
}
