package com.msntt.MSAccountService.domain.beans;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ChargedFeesReportDTO {
    private String businessPartnerId;
    private String accountNumber;
    private List<FeeCharged> feeChargedList;
}
