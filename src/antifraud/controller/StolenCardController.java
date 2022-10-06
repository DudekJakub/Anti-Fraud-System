package antifraud.controller;

import antifraud.exception.StolenCardNotFoundException;
import antifraud.mapper.StolenCardMapper;
import antifraud.model.StolenCard;
import antifraud.model.request.StolenCardRequest;
import antifraud.model.response.StolenCardCreateResponse;
import antifraud.model.response.StolenCardDeleteResponse;
import antifraud.service.StolenCardService;
import antifraud.validation.ValidCardNumber;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/antifraud/stolencard")
@AllArgsConstructor
@Validated
@PreAuthorize("hasRole('SUPPORT')")
public class StolenCardController {

    StolenCardService stolenCardService;

    @GetMapping
    List<StolenCard> listStolenCards() {
        return stolenCardService.listStolenCards();
    }

    @PostMapping
    StolenCardCreateResponse createStolenCard(@Valid @RequestBody StolenCardRequest request) {
        StolenCard card = StolenCardMapper.requestToStolenCard(request);

        log.info("Request = {}", request);
        log.info("StolenCard = {}", card);

        StolenCard savedCard = stolenCardService.register(card)
                .orElseThrow(StolenCardNotFoundException::new);

        return StolenCardMapper.stolenCardToCreateResponse(savedCard);
    }

    @DeleteMapping({"/{number}", ""})
    StolenCardDeleteResponse deleteStolenCard(@PathVariable(required = false) @ValidCardNumber String number) {
        if (number == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else if (stolenCardService.delete(number)) {
            return new StolenCardDeleteResponse(number);
        }
        throw new StolenCardNotFoundException();
    }
}
