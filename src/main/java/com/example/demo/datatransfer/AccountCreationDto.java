
package com.example.demo.datatransfer;

import com.example.demo.annotation.Password;
import com.example.demo.annotation.Username;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
https://www.javaguides.net/2018/10/user-registration-module-using-springboot-springmvc-springsecurity-hibernate5-thymeleaf-mysql.html
*/
@NoArgsConstructor @AllArgsConstructor @Data
public class AccountCreationDto {
	
	@Username
	private String username;
	
	@Password
	private String password;
	
}
