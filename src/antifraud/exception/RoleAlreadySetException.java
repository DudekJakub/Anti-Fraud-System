package antifraud.exception;

public class RoleAlreadySetException extends RuntimeException {

    public RoleAlreadySetException() {
        super("Role already set!");
    }
}
