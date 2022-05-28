package com.msntt.MSAccountService.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Credit {
    @Id
    @JsonIgnore
    private String creditid;
    @NotNull
    private String codebusinesspartner;
    @JsonIgnore
    private LocalDateTime createdate;
    @Digits(integer = 19,fraction = 6)
    private BigDecimal amountcredit;
    @Digits(integer = 19,fraction = 6)
    @JsonIgnore
    private BigDecimal usedcredit;
    @JsonIgnore
    @Digits(integer = 19,fraction = 6)
    private BigDecimal availablecredit;
    @Digits(integer = 19,fraction = 6)
    private BigDecimal paymentcredit;

}
