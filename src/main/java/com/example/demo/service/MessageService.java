
package com.example.demo.service;

import com.example.demo.domain.Account;
import com.example.demo.domain.Message;
import com.example.demo.service.repository.MessageRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
	
	@Autowired
	private MessageRepository messageRepository;
	
	public void createMessage(Account account, String content) {
		if (account != null) {
			messageRepository.save(new Message(account, content));
		}
	}
	
	public List<Message> findByAccountUsername(String username) {
		return messageRepository.findByAccountUsername(username);
	}
}
