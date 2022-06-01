package com.msntt.MSAccountService.infraestructure.services;
import com.msntt.MSAccountService.domain.model.DebitCard;
import com.msntt.MSAccountService.domain.model.Message;
import com.msntt.MSAccountService.domain.repository.DebitCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@Component
public class YankiConsumer {

    @Autowired
    private DebitCardRepository servicesdebitcard;
    @Autowired
    private StreamBridge streamBridge;
    Mono<DebitCard> _dto;

    @Bean
    Consumer<String> debitcard() {
        return message -> {
           String Id = message.toString();

        System.out.print(Id);
        };
    }

    public void sendMessage(Message message){
        streamBridge.send("output-out-0",message);
    }
    public void createdebitcardyanki(DebitCard _debitcard) {
        streamBridge.send("output-out-0", _debitcard);
    }

}
