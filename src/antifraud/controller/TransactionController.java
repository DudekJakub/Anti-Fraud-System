package antifraud.controller;

import antifraud.mapper.TransactionMapper;
import antifraud.model.Transaction;
import antifraud.model.request.TransactionFeedbackRequest;
import antifraud.model.request.TransactionRequest;
import antifraud.model.response.TransactionFeedbackResponse;
import antifraud.model.response.TransactionResultResponse;
import antifraud.service.TransactionService;
import antifraud.validation.ValidCardNumber;
import lombok.AllArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/antifraud")
@Validated
@AllArgsConstructor
public class TransactionController {

    TransactionService transactionService;
    TransactionMapper transactionMapper;

    @PreAuthorize("hasRole('SUPPORT')")
    @GetMapping("/history")
    List<TransactionFeedbackResponse> listTransactions() {
        return transactionService.listTransactions();
    }

    @PreAuthorize("hasRole('SUPPORT')")
    @GetMapping("/history/{number}")
    List<TransactionFeedbackResponse> listTransactionsByCreditNumber(@PathVariable @ValidCardNumber String number) {
        return transactionService.listTransactionsForCardNumber(number);
    }

    @PreAuthorize("hasRole('MERCHANT')")
    @PostMapping("/transaction")
    TransactionResultResponse createTransaction(@Valid @RequestBody TransactionRequest request) {
        var resultAndInfo = transactionService.process(request).entrySet().iterator().next();
        var result = resultAndInfo.getKey();
        var info = resultAndInfo.getValue();

        if (result == null || info == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return new TransactionResultResponse(result, info);
    }

    @PreAuthorize("hasRole('SUPPORT')")
    @PutMapping("/transaction")
    TransactionFeedbackResponse updateTransaction(@Valid @RequestBody TransactionFeedbackRequest request) {
        return transactionService.updateTransaction(request);
    }
}
