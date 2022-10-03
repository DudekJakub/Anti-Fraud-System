package antifraud.validation;

import antifraud.model.Result;
import antifraud.model.Transaction;
import antifraud.model.request.TransactionRequest;
import antifraud.repository.StolenCardRepository;
import antifraud.repository.SuspiciousIpRepository;
import antifraud.repository.TransactionRepository;
import antifraud.service.TransactionResponseInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class TransactionProcessValidator {

    TransactionRepository transactionRepository;
    SuspiciousIpRepository suspiciousIpRepository;
    StolenCardRepository stolenCardRepository;

    public Map<Result, String> validateProcess(TransactionRequest request) {

        var ip = request.getIp();
        var number = request.getNumber();

        var blackIpResult = suspiciousIpRepository.existsByIp(ip) ? Result.PROHIBITED : Result.ALLOWED;
        var blackCardResult = stolenCardRepository.existsByNumber(number) ? Result.PROHIBITED : Result.ALLOWED;
        var amountTooHighResult = checkResultForAmount(request);
        var regionCorrelationResult = checkResultForCorrelation(CorrelationType.REGION, request);
        var ipCorrelationResult = checkResultForCorrelation(CorrelationType.IP, request);

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
        } else if (isManual && !isProhibited) {
            return Result.MANUAL_PROCESSING;
        }
        return Result.PROHIBITED;
    }

    private String getTransactionFinalInfo(Result finalResult, Map<String, Result> mapOfGranularResults) {
        AtomicReference<String> finalInfo = new AtomicReference<>("");

        mapOfGranularResults.entrySet().stream()
                .filter(stringResultEntry -> stringResultEntry.getValue() == finalResult)
                .map(Map.Entry::getKey)
                .forEach(info -> finalInfo.set(finalInfo + info + ", "));

        return finalResult == Result.ALLOWED ? "none" : finalInfo.get().replaceAll(".{2}$", "");
    }

    private Result checkResultForAmount(TransactionRequest request) {
        var amount = request.getAmount();

        if (amount <= 200) {
            return Result.ALLOWED;
        } else if (amount <= 1500) {
            return Result.MANUAL_PROCESSING;
        }
        return Result.PROHIBITED;
    }

    private Result checkResultForCorrelation(CorrelationType correlationType, TransactionRequest request) {
        int numberOfDifferences = getNumberOfDifferentCorrelationValues(correlationType, request);

        if (numberOfDifferences < 2) {
            return Result.ALLOWED;
        } else if (numberOfDifferences == 2) {
            return Result.MANUAL_PROCESSING;
        }
        return Result.PROHIBITED;
    }

    private int getNumberOfDifferentCorrelationValues(CorrelationType correlationType, TransactionRequest request) {
        var number = request.getNumber();
        var date = request.getDate();

        var transactionsFromLastHourRelatedToNumber = transactionRepository.getOneHourOldTransactionsByNumber(number, date.minusHours(1), date);

        return switch (correlationType) {
            case IP -> transactionsFromLastHourRelatedToNumber.stream()
                    .map(Transaction::getIp)
                    .filter(ip_ -> !ip_.equals(request.getIp()))
                    .collect(Collectors.toSet())
                    .size();

            case REGION -> transactionsFromLastHourRelatedToNumber.stream()
                    .map(Transaction::getRegion)
                    .filter(reg -> !reg.equals(request.getRegion()))
                    .collect(Collectors.toSet())
                    .size();
        };
    }
}
