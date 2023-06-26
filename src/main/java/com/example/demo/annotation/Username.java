
package com.example.demo.annotation;

import com.example.demo.validator.UsernameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.FIELD;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;


@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = UsernameValidator.class)
@Documented
public @interface Username {
	
	/*
	length parameters??
	
	int min() default 1;
	int max() default 30;
	*/
	
	/*
	source: https://reflectoring.io/bean-validation-with-spring-boot/#a-custom-validator-with-spring-boot
	A custom constraint annotation needs all of the following:

	-	the parameter message, pointing to a property key in 
		ValidationMessages.properties, which is used to resolve a message in 
		case of violation
	
	-	the parameter groups, allowing to define under which circumstances this 
		validation is to be triggered
	
	-	the parameter payload, allowing to define a payload to be passed with 
		this validation
	
	-	a @Constraint annotation pointing to an implementation of the 
		ConstraintValidator interface.
	*/
	
	String message() default "{Username.invalid}";

	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };
}
