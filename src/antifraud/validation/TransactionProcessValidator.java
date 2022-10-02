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

        var amount = request.getAmount();
        var ip = request.getIp();
        var number = request.getNumber();
        var region = request.getRegion();
        var date = request.getDate();

        var transactionsRelatedToNumberFromLastHour = transactionRepository.getAllOneHourOld(number, date.minusHours(1), date);

        var numberOfDifferentRegions = transactionsRelatedToNumberFromLastHour.stream()
                .map(Transaction::getRegion)
                .filter(reg -> !reg.equals(region))
                .collect(Collectors.toSet())
                .size();

        log.info("numberOfDifferentRegions = {}", numberOfDifferentRegions);

        var numberOfDifferentIps = transactionsRelatedToNumberFromLastHour.stream()
                .map(Transaction::getIp)
                .filter(ip_ -> !ip_.equals(ip))
                .collect(Collectors.toSet())
                .size();

        log.info("numberOfDifferentIps = {}", numberOfDifferentIps);

        var isBlackIp = suspiciousIpRepository.existsByIp(ip);
        var isBlackCard = stolenCardRepository.existsByNumber(number);
        var isAmountTooHigh = amount > 1500;
        var isRegionCorrelation = numberOfDifferentRegions > 2;
        var isIpCorrelation = numberOfDifferentIps > 2;

        var mapOfChecks = new TreeMap<>(
                Map.of(
                        TransactionResponseInfo.AMOUNT, isAmountTooHigh,
                        TransactionResponseInfo.CARD, isBlackCard,
                        TransactionResponseInfo.IP, isBlackIp,
                        TransactionResponseInfo.REGION_CORRELATION, isRegionCorrelation,
                        TransactionResponseInfo.IP_CORRELATION, isIpCorrelation
                )
        );

        var responseInfoAtomic = new AtomicReference<>("");
        var transactionStatusAtomic = new AtomicReference<>(Result.ALLOWED);

        var isTransactionProhibited = mapOfChecks.values().stream().anyMatch(aBoolean -> aBoolean);

        if (!isTransactionProhibited) {
            if (amount > 200 && amount <= 1500) {
                responseInfoAtomic.set(responseInfoAtomic + TransactionResponseInfo.AMOUNT + ", ");
                transactionStatusAtomic.set(Result.MANUAL_PROCESSING);
            } else if (numberOfDifferentRegions == 2 || numberOfDifferentIps == 2 && amount <= 1500) {
                responseInfoAtomic.set(responseInfoAtomic +
                        checkCorrelationsForManualProcessing(numberOfDifferentRegions, numberOfDifferentIps));
                transactionStatusAtomic.set(Result.MANUAL_PROCESSING);
            }
        } else {
            mapOfChecks.forEach((type, aBoolean) -> {
                if (aBoolean) {
                    if (!responseInfoAtomic.toString().contains(type)) {
                        responseInfoAtomic.set(responseInfoAtomic + type + ", ");
                    }
                    transactionStatusAtomic.set(Result.PROHIBITED);
                }
            });
        }

        var returnResult = transactionStatusAtomic.get();
        var returnInfo = prepareReturnInfo(responseInfoAtomic);

        return Map.of(returnResult, returnInfo);
    }

    private String prepareReturnInfo(AtomicReference<String> responseInfoAtomic) {
        var responseInfoString = responseInfoAtomic.toString();

        if (responseInfoString.equals("")) {
            return "none";
        } else {
            return responseInfoString.replaceAll(".{2}$", "");
        }
    }

    private String checkCorrelationsForManualProcessing(int numberOfDifferentRegionsForCardNumber, int numberOfDifferentIpsForCardNumber) {
        if (numberOfDifferentRegionsForCardNumber == 2 && numberOfDifferentIpsForCardNumber == 2) {
            return TransactionResponseInfo.REGION_CORRELATION + ", " + TransactionResponseInfo.IP_CORRELATION  + ", ";
        } else if (numberOfDifferentRegionsForCardNumber == 2) {
            return TransactionResponseInfo.REGION_CORRELATION + ", ";
        } else if (numberOfDifferentIpsForCardNumber == 2) {
            return TransactionResponseInfo.IP_CORRELATION + ", ";
        }
        return "";
    }
}
