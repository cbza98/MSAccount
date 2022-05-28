package com.msntt.MSAccountService.domain.beans;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class DebitCardReportDTO {
    private String creditCardNumber;
    private String transactionType;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
}
