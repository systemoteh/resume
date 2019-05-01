package ru.systemoteh.resume.annotation.constraint;

import ru.systemoteh.resume.validator.PhoneConstraintValidator;

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
@Constraint(validatedBy = {PhoneConstraintValidator.class})
public @interface Phone {

    String message() default "Phone";

    Class<? extends Payload>[] payload() default {};

    Class<?>[] groups() default {};
}
