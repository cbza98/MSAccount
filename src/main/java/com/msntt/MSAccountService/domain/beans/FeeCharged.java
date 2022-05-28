package com.msntt.MSAccountService.domain.beans;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class FeeCharged {
    private BigDecimal commissionAmount;
    private LocalDateTime dateCharged;
}
