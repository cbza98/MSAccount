package com.msntt.MSAccountService.infraestructure.services;

import com.msntt.MSAccountService.domain.beans.AvaliableBalanceDTO;
import com.msntt.MSAccountService.domain.beans.ChargedFeesDTO;
import com.msntt.MSAccountService.domain.beans.DailyAverageBalanceDTO;
import com.msntt.MSAccountService.domain.beans.TransactionReportDTO;
import com.msntt.MSAccountService.domain.repository.AccountRepository;
import com.msntt.MSAccountService.infraestructure.interfaces.IReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ReportService implements IReportService {
    @Autowired
    private AccountRepository accRepository;

    @Override
    public Flux<TransactionReportDTO> getCreditCardTransactions(String creditCardNumber) {
        return null;
    }

    @Override
    public Flux<TransactionReportDTO> getAccountTransactions(String accountNumber) {
        return null;
    }

    @Override
    public Mono<AvaliableBalanceDTO> getCreditCardBalance(String creditCardNumber) {
        return null;
    }

    @Override
    public Mono<AvaliableBalanceDTO> getAccountBalance(String accountNumber) {
        return null;
    }

    @Override
    public Mono<DailyAverageBalanceDTO> getCreditCardDailyAverageBalance(String creditCardNumber) {
        return null;
    }

    @Override
    public Mono<DailyAverageBalanceDTO> getAccountDailyAverageBalance(String accountNumber) {
        return null;
    }

    @Override
    public Flux<ChargedFeesDTO> getCreditCardChargedFees(String creditCardNumber) {
        return null;
    }

    @Override
    public Flux<ChargedFeesDTO> getAccountChargedFees(String accountNumber) {
        return null;
    }
}
