package antifraud.exception;

public class StolenCardNumberBadFormatException extends RuntimeException {
    public StolenCardNumberBadFormatException() {
        super("Card Number has invalid format!");
    }
}
