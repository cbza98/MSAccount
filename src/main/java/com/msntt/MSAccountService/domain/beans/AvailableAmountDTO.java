package com.msntt.MSAccountService.domain.beans;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableAmountDTO {
    private String businessPartnerId;
    private String accountNumber;
    private BigDecimal availableAmount;
}
