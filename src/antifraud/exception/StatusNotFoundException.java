package antifraud.exception;

public class StatusNotFoundException extends RuntimeException {
    public StatusNotFoundException() {
        super("Incorrect status!");
    }
}
