package com.msntt.MSAccountService.domain.beans;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssociateAccountDTO {
    private String debitCardId;
    private String accountId;
}
