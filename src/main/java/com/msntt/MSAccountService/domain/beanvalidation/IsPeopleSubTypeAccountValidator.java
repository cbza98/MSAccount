package com.msntt.MSAccountService.domain.beanvalidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class IsPeopleSubTypeAccountValidator implements ConstraintValidator<IsPeopleSubTypeAccount, String> {

	List<String> subType = Arrays.asList("VI", "STD");
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		
		return subType.contains(value);
	}

}