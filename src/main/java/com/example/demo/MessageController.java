package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/*
TODO
- add
	- check for null user
*/
@Controller
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/messages")
    public String view(Model model) {
        model.addAttribute("messages", messageRepository.findAll());
        return "messages";
    }

    @PostMapping("/messages")
    public String add(@RequestParam String content) {
        if (content != null && !content.trim().isEmpty()) {
            Message msg = new Message();
            msg.setContent(content.trim());

			
			// CHECK FOR NULL USER
			
			
            // Kun käyttäjä on kirjautuneena, saa häneen liittyvän 
            // käyttäjätunnuksen ns. tietoturvakontekstista.
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            msg.setAccount(accountRepository.findByUsername(username));
            messageRepository.save(msg);
        }

        return "redirect:/messages";
    }
}
