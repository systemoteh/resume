package ru.systemoteh.resume.form;

import ru.systemoteh.resume.annotation.EnableFormErrorConversion;
import ru.systemoteh.resume.annotation.constraint.FieldMatch;
import ru.systemoteh.resume.annotation.constraint.PasswordStrength;

@FieldMatch(first = "password", second = "confirmPassword", message = "The password fields must match")
@EnableFormErrorConversion(formName = "passwordForm", fieldReference = "confirmPassword", validationAnnotationClass = FieldMatch.class)
public class PasswordForm {

    @PasswordStrength
    private String password;

    private String confirmPassword;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
