package antifraud.exception;

public class AdminAlreadyExistsException extends RuntimeException {
    public AdminAlreadyExistsException() {
        super("Admin role already exists!");
    }
}
