package antifraud.service;

import antifraud.mapper.TransactionMapper;
import antifraud.model.Result;
import antifraud.model.Transaction;
import antifraud.model.request.TransactionRequest;
import antifraud.repository.StolenCardRepository;
import antifraud.repository.SuspiciousIpRepository;
import antifraud.repository.TransactionRepository;
import antifraud.validation.TransactionProcessValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class TransactionService {

    TransactionRepository transactionRepository;
    TransactionProcessValidator transactionProcessValidator;

    public Map<Result, String> process(TransactionRequest request) {

        transactionRepository.save(TransactionMapper.requestToTransaction(request));
        log.info("New transaction saved into DB -> {}", request);

        return transactionProcessValidator.validateProcess(request);
    }
}
