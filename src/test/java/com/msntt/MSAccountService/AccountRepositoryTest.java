package com.msntt.MSAccountService;

import com.msntt.MSAccountService.domain.repository.AccountRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataMongoTest
public class AccountRepositoryTest {

    @Autowired
    AccountRepository repository;


    @Test
    public void shouldBeNotEmpty() {
        Collection<String> operations = new ArrayList<>();
        operations.add("46052675689375");
        operations.add("48767877823667");
        operations.add("73963085314026");

        System.out.println(repository.findFirstByAccountNumberInAndBalanceGreaterThanEqualOrderByDebitCardLinkDateAsc
                        (operations, BigDecimal.valueOf(10000))
                .block().toString());

    }
}
