package com.msntt.MSAccountService.infraestructure.interfaces;


import com.msntt.MSAccountService.domain.beans.ChargedFeesReportDTO;
import com.msntt.MSAccountService.domain.beans.DailyAverageBalanceDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface IReportService {

    Mono<DailyAverageBalanceDTO> getAccountDailyAverageBalance(String accountNumber);
    Flux<ChargedFeesReportDTO> getAccountChargedFees(String accountNumber, LocalDate startDate,
                                                     LocalDate endDate);

}
