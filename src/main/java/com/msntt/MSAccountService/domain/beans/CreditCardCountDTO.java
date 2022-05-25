package com.msntt.MSAccountService.domain.beans;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardCountDTO {
    private String businessPartnerCode;
    private Integer creditCardCount;
}
