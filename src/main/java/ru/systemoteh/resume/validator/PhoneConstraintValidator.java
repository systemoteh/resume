package ru.systemoteh.resume.validator;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import ru.systemoteh.resume.annotation.constraint.Phone;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PhoneConstraintValidator implements ConstraintValidator<Phone, String> {
    @Override
    public void initialize(Phone constraintAnnotation) {
    }

    @Override
    public boolean isValid(String rawNumber, ConstraintValidatorContext context) {
        if(rawNumber == null) {
            return true;
        }
        try {
            Phonenumber.PhoneNumber number = PhoneNumberUtil.getInstance().parse(rawNumber, "");
            return PhoneNumberUtil.getInstance().isValidNumber(number);
        } catch (NumberParseException e) {
            return false;
        }
    }
}