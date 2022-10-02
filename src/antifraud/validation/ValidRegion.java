package antifraud.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { RegionValidator.class })
@Documented
@ReportAsSingleViolation
public @interface ValidRegion {

    String message() default "must be one of the supported regions!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
