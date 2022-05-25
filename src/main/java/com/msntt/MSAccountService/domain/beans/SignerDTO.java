package com.msntt.MSAccountService.domain.beans;

import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignerDTO {
    private String accountNumber;
    private String signerId;
}
