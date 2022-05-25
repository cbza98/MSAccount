package com.msntt.MSAccountService.domain.validation.account;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = IsPeopleSubTypeAccountValidator.class)
@Documented
public @interface IsPeopleSubTypeAccount {
	
	String message() default "Subtype People Account is no valid. Valid values VI - Vip, STD - Standart";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
	
}
