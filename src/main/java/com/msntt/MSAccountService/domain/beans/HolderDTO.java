package com.msntt.MSAccountService.domain.beans;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HolderDTO {
    private String accountNumber;
    private String holderId;
}
