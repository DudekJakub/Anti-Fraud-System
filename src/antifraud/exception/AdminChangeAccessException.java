package antifraud.exception;

public class AdminChangeAccessException extends RuntimeException {
    public AdminChangeAccessException() {
        super("Admin cannot have changed access!");
    }
}
