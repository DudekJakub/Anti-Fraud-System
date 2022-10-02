package antifraud.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LuhnAlgorithm.class)
@Documented
public @interface ValidStolenCardNumber {

    String message() default "Card number is invalid!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
