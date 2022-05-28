package com.msntt.MSAccountService.domain.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class BusinessPartnerDTO {
	private String businessPartnerId;
	private String docType;
	private String docNum;
	private String name;
	private String type;
	private String telephone1;
	private String telephone2;
	private String contactPerson;
	private BigDecimal creditCardLine;
	private BigDecimal creditLine;
	private BigDecimal creditCard;
	private BigDecimal debitLine;
	private BigDecimal debitCard;
	private String email;
	private Boolean valid;
}
