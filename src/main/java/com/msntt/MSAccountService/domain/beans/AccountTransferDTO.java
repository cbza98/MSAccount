package com.msntt.MSAccountService.domain.beans;

import lombok.*;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountTransferDTO {

    @NotBlank
    private String faccount;
    @NotBlank
    private String taccount;
    @NotNull
    @Digits(integer =20, fraction=6)
    private BigDecimal amount;
}
