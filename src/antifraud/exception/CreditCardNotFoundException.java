package antifraud.exception;

public class CreditCardNotFoundException extends RuntimeException {
    public CreditCardNotFoundException() {
        super("Credit card not found in the database");
    }
}
