package com.msntt.MSAccountService.domain.model;

import lombok.*;

import java.math.BigDecimal;

@Data
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Message {

    private String referencia1;
    private BigDecimal amount;
    private String referencia2;
    private String referencia3;
    private String referencia4;
    private String referencia5;
    private String referencia6;

}

