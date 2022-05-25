package com.msntt.MSAccountService.domain.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class Account{

	//account field
	@Id
	private String accountNumber;
	@NotNull
	private String codeBusinessPartner;
	@Digits(integer =20, fraction=6)
	private BigDecimal balance;
	@NotNull
	private Date date_Opened;
	@NotNull	
	private Boolean valid;
	private List<Holder> holders;
	private List<Signer> signers;

	//account item fields
	@NotBlank
	private String accountName;
	@NotBlank
	private String accountType;
	@Digits(integer =20, fraction=6)
	private BigDecimal minDiaryAmount;
	@Digits(integer =20, fraction=6)
	private BigDecimal maintenanceCommission;
	@Digits(integer =20, fraction=6)
	private BigDecimal commission;
	private Integer limitTransaction;
	private Integer limitDay;
	private Boolean creditCardIsRequired;
	private List<String> businessPartnerAllowed;
	private Integer limitAccountsAllowed;
	private Boolean moreHoldersAreAllowed;
	private Boolean signersAreAllowed;
	private Boolean hasAccountsLimit;

}