package com.msntt.MSAccountService.infraestructure.interfaces;


import com.msntt.MSAccountService.domain.beans.AvaliableBalanceDTO;
import com.msntt.MSAccountService.domain.beans.ChargedFeesDTO;
import com.msntt.MSAccountService.domain.beans.DailyAverageBalanceDTO;
import com.msntt.MSAccountService.domain.beans.TransactionReportDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IReportService {

    Flux<TransactionReportDTO> getCreditCardTransactions(String creditCardNumber);
    Flux<TransactionReportDTO> getAccountTransactions(String accountNumber);
    Mono<AvaliableBalanceDTO> getCreditCardBalance(String creditCardNumber);
    Mono<AvaliableBalanceDTO> getAccountBalance(String accountNumber);
    Mono<DailyAverageBalanceDTO> getCreditCardDailyAverageBalance(String creditCardNumber);
    Mono<DailyAverageBalanceDTO> getAccountDailyAverageBalance(String accountNumber);
    Flux<ChargedFeesDTO> getCreditCardChargedFees(String creditCardNumber);
    Flux<ChargedFeesDTO> getAccountChargedFees(String accountNumber);
}
