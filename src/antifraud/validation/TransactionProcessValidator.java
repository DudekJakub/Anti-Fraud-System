package antifraud.validation;

import antifraud.exception.CreditCardNotFoundException;
import antifraud.model.Result;
import antifraud.model.Transaction;
import antifraud.model.request.TransactionRequest;
import antifraud.repository.CreditCardRepository;
import antifraud.repository.StolenCardRepository;
import antifraud.repository.SuspiciousIpRepository;
import antifraud.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class TransactionProcessValidator {

    TransactionRepository transactionRepository;
    SuspiciousIpRepository suspiciousIpRepository;
    StolenCardRepository stolenCardRepository;

    CreditCardRepository creditCardRepository;

    public Map<Result, String> validateProcess(TransactionRequest request) {

        var ip = request.getIp();
        var number = request.getNumber();

        var blackIpResult = suspiciousIpRepository.existsByIp(ip) ? Result.PROHIBITED : Result.ALLOWED;
        var blackCardResult = stolenCardRepository.existsByNumber(number) ? Result.PROHIBITED : Result.ALLOWED;
        var amountTooHighResult = checkResultForAmount(request);
        var regionCorrelationResult = checkResultForCorrelation(Transaction::getRegion, request);
        var ipCorrelationResult = checkResultForCorrelation(Transaction::getIp, request);

        var mapOfGranularResults = new TreeMap<>(
                Map.of(
                        TransactionResponseInfo.AMOUNT, amountTooHighResult,
                        TransactionResponseInfo.CARD, blackCardResult,
                        TransactionResponseInfo.IP, blackIpResult,
                        TransactionResponseInfo.REGION_CORRELATION, regionCorrelationResult,
                        TransactionResponseInfo.IP_CORRELATION, ipCorrelationResult
                )
        );

        var returnResult = getTransactionFinalResult(mapOfGranularResults);
        var returnInfo = getTransactionFinalInfo(returnResult, mapOfGranularResults);

        return Map.of(returnResult, returnInfo);
    }

    private Result getTransactionFinalResult(Map<String, Result> mapOfGranularResults) {
        var isAllowed = mapOfGranularResults.values().stream().allMatch(result -> result == Result.ALLOWED);
        var isManual = mapOfGranularResults.values().stream().anyMatch(result -> result == Result.MANUAL_PROCESSING);
        var isProhibited = mapOfGranularResults.values().stream().anyMatch(result -> result == Result.PROHIBITED);

        if (isAllowed) {
            return Result.ALLOWED;
        }
        return isManual && !isProhibited ? Result.MANUAL_PROCESSING : Result.PROHIBITED;
    }

    private String getTransactionFinalInfo(Result finalResult, Map<String, Result> mapOfGranularResults) {
        StringBuilder finalInfo = new StringBuilder("");

        mapOfGranularResults.entrySet().stream()
                .filter(stringResultEntry -> stringResultEntry.getValue() == finalResult)
                .map(Map.Entry::getKey)
                .forEach(info -> finalInfo.append(info).append(", "));

        finalInfo.setLength(finalInfo.length() - 2);

        return finalResult == Result.ALLOWED ? "none" : finalInfo.toString();
    }

    private Result checkResultForAmount(TransactionRequest request) {
        var amount = request.getAmount();
        var creditCardNumber = request.getNumber();

        var creditCard = creditCardRepository.findByCardNumber(creditCardNumber)
                .orElseThrow(CreditCardNotFoundException::new);

        var cardMaxAllowedLimit = creditCard.getAllowedLimit();
        var cardMaxManualProcessingLimit = creditCard.getManualProcessingLimit();

        if (amount <= cardMaxAllowedLimit) {
            return Result.ALLOWED;
        }
        return amount <= cardMaxManualProcessingLimit ? Result.MANUAL_PROCESSING : Result.PROHIBITED;
    }

    private Result checkResultForCorrelation(Function<Transaction, ?> mapper, TransactionRequest request) {
        var number = request.getNumber();
        var date = request.getDate();

        var transactionsFromLastHourRelatedToNumber = transactionRepository.findByNumberAndDateBetween(number, date.minusHours(1), date);

        long numberOfDiffs = transactionsFromLastHourRelatedToNumber.stream()
                .map(mapper)
                .collect(Collectors.toSet())
                .size() - 1;

        if (numberOfDiffs < 2) {
            return Result.ALLOWED;
        }
        return numberOfDiffs == 2 ? Result.MANUAL_PROCESSING : Result.PROHIBITED;
    }

    private static class TransactionResponseInfo {
        public static final String IP = "ip";
        public static final String CARD = "card-number";
        public static final String AMOUNT = "amount";
        public static final String REGION_CORRELATION = "region-correlation";
        public static final String IP_CORRELATION = "ip-correlation";
    }
}
