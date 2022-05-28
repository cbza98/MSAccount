package com.msntt.MSAccountService.infraestructure.interfaces;

import com.msntt.MSAccountService.domain.beans.ChargedFeesReportDTO;
import com.msntt.MSAccountService.domain.beans.DailyAverageBalanceDTO;
import com.msntt.MSAccountService.domain.beans.DebitCardReportDTO;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface IReportService {
    Mono<DailyAverageBalanceDTO> getAccountDailyAverageBalance(String accountNumber);
    Mono<ChargedFeesReportDTO> getAccountChargedFees(String accountNumber, LocalDate startDate, LocalDate endDate);
    Mono<DebitCardReportDTO> getDebitCardReport(String debitCardNumber);
}
