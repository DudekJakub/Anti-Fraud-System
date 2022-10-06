package antifraud.model;

public enum Role {
    ADMINISTRATOR, MERCHANT, SUPPORT;

    @Override
    public String toString() {
        return "ROLE_" + name();
    }
}
