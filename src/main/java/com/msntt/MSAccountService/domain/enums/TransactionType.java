package com.msntt.MSAccountService.domain.enums;

public enum TransactionType {
    DEPOSIT("01"),
    WITHDRAWAL("02"),
    THIRD_PARTY_TRANSFER("03"),
    SAME_HOLDER_TRANSFER("04"),
    CREDIT_PAYMENT("05"),
    CREDIT_CARD_PAYMENT("06"),
    CREDIT_CARD_CONSUMPTION("07"),
    CREDIT_CONSUMPTION("08"),
    DEBIT_CARD_CONSUMPTION("09");

    public final String type;

    TransactionType(String type) {
        this.type = type;
    }
}
