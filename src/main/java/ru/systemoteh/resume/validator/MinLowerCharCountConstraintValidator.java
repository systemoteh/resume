package ru.systemoteh.resume.validator;

import ru.systemoteh.resume.annotation.constraint.MinLowerCharCount;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MinLowerCharCountConstraintValidator implements ConstraintValidator<MinLowerCharCount, CharSequence> {

    private int minValue;

    @Override
    public void initialize(MinLowerCharCount constraintAnnotation) {
        minValue = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        int count = 0;
        for (int i = 0; i < value.length(); i++) {
            if (Character.isLowerCase(value.charAt(i))) {
                count++;
            }
        }
        return count >= minValue;
    }
}