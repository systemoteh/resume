package ru.systemoteh.resume.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.SafeHtml;
import org.hibernate.validator.constraints.URL;
import ru.systemoteh.resume.annotation.constraint.EnglishLanguage;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Setter
@Embeddable
@Access(AccessType.FIELD)
public class Contact implements Serializable {

    @SafeHtml
    @EnglishLanguage
    @Column(name = "skype", nullable = true, length = 255)
    private String skype;

    @EnglishLanguage
    @URL(host="vk.com")
    @Column(name = "vkontakte", nullable = true, length = 255)
    private String vkontakte;

    @EnglishLanguage
    @URL(host="facebook.com")
    @Column(name = "facebook", nullable = true, length = 255)
    private String facebook;

    @EnglishLanguage
    @URL(host="linkedin.com")
    @Column(name = "linkedin", nullable = true, length = 255)
    private String linkedin;

    @EnglishLanguage
    @URL(host="github.com")
    @Column(name = "github", nullable = true, length = 255)
    private String github;

    @EnglishLanguage
    @URL(host="stackoverflow.com")
    @Column(name = "stackoverflow", nullable = true, length = 255)
    private String stackoverflow;

}
