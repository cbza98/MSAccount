package com.msntt.MSAccountService.infraestructure.services;

import com.msntt.MSAccountService.domain.beans.ChargedFeesReportDTO;
import com.msntt.MSAccountService.domain.beans.DailyAverageBalanceDTO;
import com.msntt.MSAccountService.domain.beans.DebitCardReportDTO;
import com.msntt.MSAccountService.domain.repository.TransactionRepository;
import com.msntt.MSAccountService.infraestructure.interfaces.IReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
public class ReportService implements IReportService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public Mono<DailyAverageBalanceDTO> getAccountDailyAverageBalance(String accountNumber) {
        return null;
    }

    @Override
    public Mono<ChargedFeesReportDTO> getAccountChargedFees(String accountNumber, LocalDate startDate,
                                                            LocalDate endDate){
            return null;
    }
    @Override
    public Mono<DebitCardReportDTO> getDebitCardReport(String debitCardNumber) {
        return null;
    }




}
