package com.msntt.MSAccountService.domain.beans;

import lombok.*;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionReportDTO {
    @NotBlank
    private Date transactionDate;
    @Digits(integer =20, fraction=6)
    private BigDecimal debit;
    @Digits(integer =20, fraction=6)
    private BigDecimal credit;
}
