package com.msntt.MSAccountService.domain.beans;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DebitCardPayDTO {
    private String debiCardNumber;
    private String cvv;
    private String expireDate;
    private BigDecimal payAmount;
}
