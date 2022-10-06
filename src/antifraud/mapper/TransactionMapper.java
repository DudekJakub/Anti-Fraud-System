package antifraud.mapper;

import antifraud.model.Transaction;
import antifraud.model.request.TransactionRequest;
import antifraud.model.response.TransactionFeedbackResponse;
import antifraud.service.CreditCardService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TransactionMapper {

    ModelMapper modelMapper;

    public Transaction requestToEntity(CreditCardService creditCardService, TransactionRequest source) {
        modelMapper.typeMap(TransactionRequest.class, Transaction.class)
                .addMappings(mapper -> mapper.map(
                        src -> creditCardService.findByNumberOrCreateNew(source.getNumber()),
                        Transaction::setCreditCard
                        ));

        return modelMapper.map(source, Transaction.class);
    }

    public TransactionFeedbackResponse entityToResponse(Transaction source) {
        modelMapper.typeMap(Transaction.class, TransactionFeedbackResponse.class)
                .addMappings(mapper -> mapper.map(
                        src -> source.getCreditCard().getCardNumber(),
                        TransactionFeedbackResponse::setNumber
                ));
//                .addMappings(mapper -> mapper.map(
//                        src -> source.getFeedback(),
//                        (destination, value) -> {
//                            if (source.getFeedback() == null) {
//                                destination.setFeedback("");
//                            } else {
//                                destination.setFeedback(source.getFeedback().toString());
//                            }
//                        }));

        return modelMapper.map(source, TransactionFeedbackResponse.class);
    }
}
