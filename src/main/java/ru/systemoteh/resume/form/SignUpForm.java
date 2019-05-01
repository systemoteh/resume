package ru.systemoteh.resume.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.SafeHtml;
import ru.systemoteh.resume.annotation.constraint.EnglishLanguage;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpForm extends PasswordForm {

    @NotNull
    @Size(max = 50)
    @SafeHtml
    @EnglishLanguage(withNumbers = false, withSpecialSymbols = false)
    private String firstName;

    @NotNull
    @Size(max = 50)
    @SafeHtml
    @EnglishLanguage(withNumbers = false, withSpecialSymbols = false)
    private String lastName;
}
