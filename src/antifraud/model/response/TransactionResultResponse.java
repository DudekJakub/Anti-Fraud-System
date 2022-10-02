package antifraud.model.response;

import antifraud.model.Result;
import lombok.Value;


@Value
public class TransactionResultResponse {

    Result result;
    String info;
}