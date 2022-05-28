package com.msntt.MSAccountService.domain.beans;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssociateAccountDTO {
    @NotBlank
    private String debitCardId;
    @NotBlank
    private String accountId;
    @NotNull
    private Boolean isNewMainAccount;
}
