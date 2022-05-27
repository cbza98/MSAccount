package com.msntt.MSAccountService.domain.beanvalidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class ValidaccountholderValidator implements ConstraintValidator<Validaccountholder, String> {

	List<String> type = Arrays.asList("AH", "CO", "PL", "VI", "PY");

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return type.contains(value);
	}
}
