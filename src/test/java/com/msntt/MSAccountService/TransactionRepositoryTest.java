package com.msntt.MSAccountService;

import com.msntt.MSAccountService.domain.repository.TransactionRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
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
        System.out.println(repository.countByCreateDateBetween(
                YearMonth.now().atDay(1),YearMonth.now().atEndOfMonth()).block());
        System.out.println(repository.countByAccountAndTransactiontypeIn("48767877823667",operations).block());

        assertThat(repository.countByAccountAndTransactiontypeIn(
                "46052675689375",operations).block()).isNotNull();
    }
}