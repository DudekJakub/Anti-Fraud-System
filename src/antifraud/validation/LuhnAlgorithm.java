package antifraud.validation;

import antifraud.exception.StolenCardNumberBadFormatException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class LuhnAlgorithm implements ConstraintValidator<ValidCardNumber, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return isCardNumberValid(value);
    }

    public static boolean isCardNumberValid(String cardNumber) {
        if (cardNumber == null) {
            throw new StolenCardNumberBadFormatException();
        }

        var numbers = stringToIntArray(cardNumber);
        var numbersSize = numbers.length;

        for (int i = 0; i < numbersSize; i++) {
            if (i % 2 == 0) {
                numbers[i] *= 2;
                var oddNUmber = numbers[i];
                if (oddNUmber > 9) {
                    numbers[i] = Arrays.stream(stringToIntArray(String.valueOf(oddNUmber))).sum();
                }
            }
        }
        int sumOfDigits = Arrays.stream(numbers).sum();

        return sumOfDigits % 10 == 0;
    }

    public static int[] stringToIntArray(String string) {
        return string.chars()
                .map(c -> c - '0')
                .toArray();
    }
}
