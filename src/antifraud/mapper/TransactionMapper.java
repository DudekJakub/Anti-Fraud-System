package antifraud.mapper;

import antifraud.model.Transaction;
import antifraud.model.request.TransactionRequest;

public class TransactionMapper {

    public static Transaction requestToTransaction(TransactionRequest request) {
        return Transaction.builder()
                .amount(request.getAmount())
                .ip(request.getIp())
                .number(request.getNumber())
                .region(request.getRegion())
                .date(request.getDate())
                .build();
    }
}
