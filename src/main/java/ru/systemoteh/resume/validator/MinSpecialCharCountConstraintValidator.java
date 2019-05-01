package ru.systemoteh.resume.validator;

import ru.systemoteh.resume.annotation.constraint.MinSpecialCharCount;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MinSpecialCharCountConstraintValidator implements ConstraintValidator<MinSpecialCharCount, CharSequence> {

    private int minValue;
    private String specSymbols;

    @Override
    public void initialize(MinSpecialCharCount constraintAnnotation) {
        minValue = constraintAnnotation.value();
        specSymbols = constraintAnnotation.specSymbols();
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        int count = 0;
        for (int i = 0; i < value.length(); i++) {
            if (specSymbols.indexOf(value.charAt(i)) != -1) {
                count++;
            }
        }
        return count >= minValue;
    }
}