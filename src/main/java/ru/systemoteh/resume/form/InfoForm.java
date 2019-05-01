package ru.systemoteh.resume.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.SafeHtml;
import ru.systemoteh.resume.annotation.constraint.EnglishLanguage;
import ru.systemoteh.resume.domain.Profile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InfoForm {

    @EnglishLanguage
    @SafeHtml
    private String info;

    public InfoForm(Profile profile) {
        super();
        this.info = profile.getInfo();
    }

}
