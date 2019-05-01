package ru.systemoteh.resume.validator;

import ru.systemoteh.resume.annotation.constraint.MinUpperCharCount;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MinUpperCharCountConstraintValidator implements ConstraintValidator<MinUpperCharCount, CharSequence> {

    private int minValue;

    @Override
    public void initialize(MinUpperCharCount constraintAnnotation) {
        minValue = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        int count = 0;
        for (int i = 0; i < value.length(); i++) {
            if (Character.isUpperCase(value.charAt(i))) {
                count++;
            }
        }
        return count >= minValue;
    }
}