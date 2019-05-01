package ru.systemoteh.resume.annotation.constraint;

import ru.systemoteh.resume.validator.FirstFieldLessThanSecondConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = FirstFieldLessThanSecondConstraintValidator.class)
@Documented
public @interface FirstFieldLessThanSecond {

    String message() default "FirstFieldLessThanSecond";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String first();

    String second();

    /**
     * Defines several <code>@FieldMatch</code> annotations on the same element
     *
     * @see FirstFieldLessThanSecond
     */
    @Target({TYPE, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        FirstFieldLessThanSecond[] value();
    }
}
