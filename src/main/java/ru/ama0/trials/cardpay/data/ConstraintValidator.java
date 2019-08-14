package ru.ama0.trials.cardpay.data;

import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
// @Scope(SCOPE_PROTOTYPE)
public class ConstraintValidator<T> {

    @Getter
    private Validator validator;

    public ConstraintValidator() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    public List<String> validate(T record) {
        Set<ConstraintViolation<T>> validates = validator.validate(record);
        return validates.stream().map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
    }

}
