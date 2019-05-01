package ru.systemoteh.resume.annotation.constraint;

import ru.systemoteh.resume.validator.MinSpecialCharCountConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {MinSpecialCharCountConstraintValidator.class})
public @interface MinSpecialCharCount {

    int value() default 1;

    String specSymbols() default "!@~`#$%^&*()_-+=|\\/{}[].,;:/?";

    String message() default "MinSpecialCharCount";

    Class<? extends Payload>[] payload() default {};

    Class<?>[] groups() default {};
}
