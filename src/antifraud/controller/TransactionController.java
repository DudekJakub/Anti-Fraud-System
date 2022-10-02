package antifraud.controller;

import antifraud.model.request.TransactionRequest;
import antifraud.model.response.TransactionResultResponse;
import antifraud.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/antifraud")
@AllArgsConstructor
public class TransactionController {
    TransactionService transactionService;

    @PreAuthorize("hasAnyAuthority('MERCHANT')")
    @PostMapping("/transaction")
    TransactionResultResponse transactionPost(@Valid @RequestBody TransactionRequest request) {
        var resultAndInfo = transactionService.process(request).entrySet().iterator().next();
        var result = resultAndInfo.getKey();
        var info = resultAndInfo.getValue();

        if (result == null || info == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return new TransactionResultResponse(result, info);
    }
}
