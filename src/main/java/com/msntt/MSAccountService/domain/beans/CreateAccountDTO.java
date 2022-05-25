package com.msntt.MSAccountService.domain.beans;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountDTO {
    @NotNull
    private String codeBusinessPartner;
    @NotNull
    private String accountCode;
}
