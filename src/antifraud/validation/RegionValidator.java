package antifraud.validation;

import antifraud.model.Region;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

@Component
@AllArgsConstructor
public class RegionValidator implements ConstraintValidator<ValidRegion, String> {

    ApplicationContext applicationContext;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        var validRegions = (List<Region>) applicationContext.getBean("validRegions");

        return validRegions.stream()
                .map(Region::getName)
                .anyMatch(reg -> reg.equals(value));
    }
}
