
package com.example.demo.datatransfer;

import com.example.demo.annotation.Password;
import com.example.demo.annotation.Username;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
https://www.javaguides.net/2018/10/user-registration-module-using-springboot-springmvc-springsecurity-hibernate5-thymeleaf-mysql.html

(We use AccountCreationDto class to transfer the data between the controller layer and 
the view layer.) We (also) use AccountDto class for form binding.
*/
@NoArgsConstructor @AllArgsConstructor @Data
public class AccountCreationDto {
	
	@Username
	private String username;
	
	@Password
	private String password;
	
	public String toString() {
		return "AccountCreationDto={username='" + username + "', password='" + password +"'}";
	}
}
