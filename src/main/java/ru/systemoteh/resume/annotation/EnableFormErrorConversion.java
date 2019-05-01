package ru.systemoteh.resume.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Adds rules for conversion from form validation to field validation for display errors on UI.
 *
 * For example, class ru.systemoteh.resume.domain.Education has @FirstFieldLessThanSecond
 * and @EnableFormErrorConversion validation annotations
 * which convert @FirstFieldLessThanSecond form error ru.systemoteh.resume.form.EducationForm
 * to error for ru.systemoteh.resume.domain.Education finishYear field
 *
 * Please look at ru.systemoteh.resume.component.impl.FormErrorConverterImpl component for details
 */

@Target({TYPE})
@Retention(RUNTIME)
@Documented
public @interface EnableFormErrorConversion {

    String formName();

    String fieldReference();

    Class<? extends Annotation> validationAnnotationClass();

    @Target({TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        EnableFormErrorConversion[] value();
    }
}