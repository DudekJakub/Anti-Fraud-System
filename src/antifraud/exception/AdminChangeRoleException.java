package antifraud.exception;

public class AdminChangeRoleException extends RuntimeException {
    public AdminChangeRoleException() {
        super("Admin cannot have changed role!");
    }
}
