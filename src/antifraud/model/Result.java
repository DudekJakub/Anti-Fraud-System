package antifraud.model;

import lombok.Getter;

/**
 NOTE: This class maintains order of enums which is crucial for updating credit-card's limits.
        Please, be aware of that while changing the order!
 */

@Getter
public enum Result {
    PROHIBITED(2), ALLOWED(0), MANUAL_PROCESSING(1);

    private final int hierarchy;
    Result(final int hierarchy) {
        this.hierarchy = hierarchy;
    }
}