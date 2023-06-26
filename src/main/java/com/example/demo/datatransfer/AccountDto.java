
package com.example.demo.datatransfer;

import com.example.demo.annotation.Username;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
https://www.javaguides.net/2018/10/user-registration-module-using-springboot-springmvc-springsecurity-hibernate5-thymeleaf-mysql.html

(We use AccountDto class to transfer the data between the controller layer and 
the view layer.) We (also) use AccountDto class for form binding.
*/
@NoArgsConstructor @AllArgsConstructor @Data
public class AccountDto {
	
	@Username
	private String username;
	
	@NotEmpty
	@Size(min = 1, max = 20)
	private String password;
}
