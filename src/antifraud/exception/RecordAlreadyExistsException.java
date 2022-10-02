package antifraud.exception;

public class RecordAlreadyExistsException extends RuntimeException {
    public RecordAlreadyExistsException() {
        super("Record already exists in database");
    }
}
