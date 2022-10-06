package antifraud.service;

import antifraud.exception.TransactionNotFoundException;
import antifraud.mapper.TransactionMapper;
import antifraud.model.CreditCard;
import antifraud.model.Result;
import antifraud.model.Transaction;
import antifraud.model.request.TransactionFeedbackRequest;
import antifraud.model.request.TransactionRequest;
import antifraud.model.response.TransactionFeedbackResponse;
import antifraud.repository.TransactionRepository;
import antifraud.validation.TransactionProcessValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static antifraud.model.Result.ALLOWED;
import static antifraud.model.Result.PROHIBITED;
import static antifraud.service.CreditCardLimitType.*;
import static antifraud.service.CreditCardOperationType.*;

import static org.springframework.data.domain.Sort.sort;

@Slf4j
@Service
@AllArgsConstructor
public class TransactionService {

    TransactionRepository transactionRepository;
    CreditCardService creditCardService;
    TransactionProcessValidator transactionProcessValidator;
    TransactionMapper transactionMapper;

    public List<TransactionFeedbackResponse> listTransactions() {
        return transactionRepository.findAll(
                        sort(Transaction.class).by(Transaction::getIp).ascending()
                )
                .stream()
                .sorted(Comparator.comparing(Transaction::getId))
                .map(transaction -> transactionMapper.entityToResponse(transaction))
                .filter(trans -> {
                    if (trans.getFeedback() == null) {
                        trans.setFeedback("");
                    }
                    return true;
                })
                .toList();
    }

    public List<TransactionFeedbackResponse> listTransactionsForCardNumber(String cardNumber) {
        var transactionsByNumber = transactionRepository.findByNumber(cardNumber)
                .stream()
                .sorted(Comparator.comparing(Transaction::getId))
                .map(transaction -> transactionMapper.entityToResponse(transaction))
                .toList();

        if (transactionsByNumber.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        transactionsByNumber.forEach(trans -> { if (trans.getFeedback() == null) trans.setFeedback("");});

        return transactionsByNumber;
    }

    public Map<Result, String> process(TransactionRequest request) {

        var savedTransaction = transactionRepository.save(
                transactionMapper.requestToEntity(creditCardService, request));

        log.info("New transaction saved into DB -> {}", request);
        log.info("Transaction from DB -> {}", savedTransaction);

        var returnInfo = transactionProcessValidator.validateProcess(request);
        var result = returnInfo.entrySet().iterator().next().getKey();

        savedTransaction.setResult(result);
        transactionRepository.save(savedTransaction);

        log.info("Transaction from DB -> {}", savedTransaction);

        return returnInfo;
    }

    public TransactionFeedbackResponse updateTransaction(TransactionFeedbackRequest request) {

        var transaction = transactionRepository.findById(request.getTransactionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (transaction.getFeedback() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        var requestFeedback = request.getFeedback();
        var creditCard = transaction.getCreditCard();

        updateCreditCardLimits(creditCard, requestFeedback, transaction);

        transaction.setFeedback(Result.valueOf(request.getFeedback()));

        var updateTransaction = transactionRepository.save(transaction);

        log.info("UPDATED CREDIT CARD: {}", updateTransaction.getCreditCard());

        return transactionMapper.entityToResponse(updateTransaction);
    }

    private void updateCreditCardLimits(CreditCard creditCard, String feedback, Transaction transaction) {

        var transactionFeedback = Result.valueOf(feedback);
        var transactionValidity = transaction.getResult();
        var transactionAmount = transaction.getAmount();
        var sing = Math.signum(transactionValidity.getHierarchy() - transactionFeedback.getHierarchy());

        log.info("TRANSACTION VALIDITY ({}) ORDINAL = {}", transactionValidity, transactionValidity.ordinal());
        log.info("TRANSACTION FEEDBACK ({}) ORDINAL = {}", transactionFeedback, transactionFeedback.ordinal());

        if (sing == 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (ALLOWED == transactionValidity || ALLOWED == transactionFeedback) {
            creditCard.setAllowedLimit(Math.ceil(0.8 * creditCard.getAllowedLimit() + sing * 0.2 * transactionAmount));
        }
        if (PROHIBITED == transactionValidity || PROHIBITED == transactionFeedback) {
            creditCard.setManualProcessingLimit(Math.ceil(0.8 * creditCard.getManualProcessingLimit() + sing * 0.2 * transactionAmount));
        }
    }
}
