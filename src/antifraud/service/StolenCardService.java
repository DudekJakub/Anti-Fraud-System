package antifraud.service;

import antifraud.exception.RecordAlreadyExistsException;
import antifraud.model.StolenCard;
import antifraud.repository.StolenCardRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class StolenCardService {

    StolenCardRepository stolenCardRepository;

    public List<StolenCard> listStolenCards() {
        return stolenCardRepository.findAll(
                Sort.sort(StolenCard.class).by(StolenCard::getId).ascending()
        );
    }

    @Transactional
    public Optional<StolenCard> register(StolenCard stolenCard) {

        if (stolenCardRepository.existsByNumber(stolenCard.getNumber())) {
            throw new RecordAlreadyExistsException();
        }

        return Optional.of(stolenCardRepository.save(stolenCard));
    }

    @Transactional
    public boolean delete(String stolenCardNumber) {
        return stolenCardRepository.deleteByNumber(stolenCardNumber) == 1;
    }
}
