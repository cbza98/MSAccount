package com.msntt.MSAccountService.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class LinkedAccount{
    @Id
    private String accountId;
    private Date addedDate;
}
