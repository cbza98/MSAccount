package com.msntt.MSAccountService.domain.beans;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
public class FeeCharged {
    @Field("commissionAmount")
    private BigDecimal commissionAmount;
    @Field("createDate")
    private LocalDateTime createDate;
}
