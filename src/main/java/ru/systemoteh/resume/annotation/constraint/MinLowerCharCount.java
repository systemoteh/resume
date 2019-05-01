package ru.systemoteh.resume.annotation.constraint;

import ru.systemoteh.resume.validator.MinLowerCharCountConstraintValidator;

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
@Constraint(validatedBy = {MinLowerCharCountConstraintValidator.class})
public @interface MinLowerCharCount {

    int value() default 1;

    String message() default "MinLowerCharCount";

    Class<? extends Payload>[] payload() default {};

    Class<?>[] groups() default {};
}