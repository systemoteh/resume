package ru.systemoteh.resume.validator;

import org.springframework.beans.BeanUtils;
import ru.systemoteh.resume.annotation.constraint.FieldMatch;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * https://github.com/Rudeg/Spring-MVC-Example/blob/master/Lab%202/src/main/java/com/springexample/common/constraits/impl/FieldMatchValidator.java
 */
public class FieldMatchConstraintValidator implements ConstraintValidator<FieldMatch, Object> {
    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(final FieldMatch constraintAnnotation) {
        firstFieldName = constraintAnnotation.first();
        secondFieldName = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        try {
            Object firstValue  = BeanUtils.getPropertyDescriptor(value.getClass(), firstFieldName).getReadMethod().invoke(value);
            Object secondValue = BeanUtils.getPropertyDescriptor(value.getClass(), secondFieldName).getReadMethod().invoke(value);
            return (firstValue == null && secondValue == null) || (firstValue != null && firstValue.equals(secondValue));
        } catch (Exception ignore) {
            return false;
        }
    }
}