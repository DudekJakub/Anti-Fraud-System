package antifraud.mapper;

import antifraud.model.StolenCard;
import antifraud.model.request.StolenCardRequest;
import antifraud.model.response.StolenCardCreateResponse;

public class StolenCardMapper {

    public static StolenCard requestToStolenCard(StolenCardRequest request) {
        return StolenCard.builder()
                .number(request.getNumber())
                .build();
    }

    public static StolenCardCreateResponse stolenCardToCreateResponse(StolenCard stolenCard) {
        return StolenCardCreateResponse.builder()
                .id(stolenCard.getId())
                .number(stolenCard.getNumber())
                .build();
    }
}
