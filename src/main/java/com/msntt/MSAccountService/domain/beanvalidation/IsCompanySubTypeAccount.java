package com.msntt.MSAccountService.domain.beanvalidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = IsCompanySubTypeAccountValidator.class)
@Documented
public @interface IsCompanySubTypeAccount {
	
	String message() default "Subtype Company Account is no valid. Valid values PY - Pyme, STD - Standart";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
	
}
