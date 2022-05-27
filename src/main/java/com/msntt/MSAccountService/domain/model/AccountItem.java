package com.msntt.MSAccountService.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class AccountItem {

    @Id
    private String itemCode;

    @NotBlank
    private String accountName;
    @NotBlank
    private String accountType;
    @NotNull
    private Boolean valid;
    @NotNull
    @Digits(integer =20, fraction=6)
    private BigDecimal minDiaryAmount;
    @NotNull
    @Digits(integer =20, fraction=6)
    private BigDecimal maintenanceCommission;
    @NotNull
    @Digits(integer =20, fraction=6)
    private BigDecimal commission;
    @NotNull
    private Integer limitTransaction;
    @NotNull
    private Integer limitDay;
    @NotNull
    private Boolean creditCardIsRequired;
    @NotEmpty
    private List<String> businessPartnerAllowed;
    @NotNull
    private Integer limitAccountsAllowed;
    @NotNull
    private Boolean moreHoldersAreAllowed;
    @NotNull
    private Boolean signersAreAllowed;
    @NotNull
    private Boolean hasAccountsLimit;
}
