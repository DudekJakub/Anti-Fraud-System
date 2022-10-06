package antifraud.service;

import antifraud.model.CreditCard;
import antifraud.repository.CreditCardRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Slf4j
@AllArgsConstructor
public class CreditCardService {

    CreditCardRepository creditCardRepository;

    @Transactional
    public CreditCard findByNumberOrCreateNew(String cardNumber) {
        var creditCard = creditCardRepository.findByCardNumber(cardNumber);

        return creditCard.orElseGet(() -> creditCardRepository.save(new CreditCard(cardNumber)));
    }
}
