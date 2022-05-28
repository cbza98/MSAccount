package com.msntt.MSAccountService.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class DebitCard {

    @Id
    private String cardNumber;
    @NotNull
    private String cardName;
    private String expiringDate;
    List<LinkedAccount> linkedAccountList;
    @NotNull
    private String cvv;
    @NotNull
    private Boolean valid;
    @NotNull
    private LocalDateTime createdate;
    @NotNull
    private String codeBusinessPartner;
}
