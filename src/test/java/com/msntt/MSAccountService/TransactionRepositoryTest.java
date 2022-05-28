package com.msntt.MSAccountService;

import com.msntt.MSAccountService.domain.repository.TransactionRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataMongoTest
public class TransactionRepositoryTest {

    @Autowired
    TransactionRepository repository;

    @Test
    public void shouldBeNotEmpty() {
        Collection<String> operations = new ArrayList<>();
        operations.add("DEPOSIT");
        operations.add("WITHDRAWAL");

        System.out.println(repository.countByAccountAndTransactiontypeInAndCreateDateBetween(
                "48767877823667",operations,YearMonth.now().atDay(1),YearMonth.now().atEndOfMonth()).block());

        assertThat(repository.countByAccountAndTransactiontypeIn(
                "46052675689375",operations).block()).isNotNull();
    }

    @Test
    public void shouldBeNotEmpty2() {

        repository.findByDebitCardIdOrderByCreateDateDesc("6539-9822-2872-2389").take(10)
                .collectList().block().forEach(list->System.out.println(list.toString()));
    }

    @Test
    public void shouldBeNotEmpty3() {
        LocalDate start = YearMonth.now().atDay(1);
        LocalDate end = YearMonth.now().atEndOfMonth();

        repository.findByAccountAndCreateDateBetweenAndCommissionAmountGreaterThan("48767877823667",
                                                                            start,end, BigDecimal.ZERO)
                .collectList().block().forEach(list->System.out.println(list.toString()));
    }
}