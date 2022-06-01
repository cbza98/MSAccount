package com.msntt.MSAccountService.domain.beans;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class DebitCardDTO {
    private String cardNumber;
    private String cardName;
    private String expiringDate;
    private String cvv;
    private Boolean valid;
    private String codeBusinessPartner;
}
