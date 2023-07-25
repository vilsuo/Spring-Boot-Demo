
package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultController {
	
	@GetMapping("/")
	public String home() {
		return "redirect:/accounts";
	}
}
