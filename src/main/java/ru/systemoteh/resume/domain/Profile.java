package ru.systemoteh.resume.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.SafeHtml;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.springframework.data.elasticsearch.annotations.Document;
import ru.systemoteh.resume.annotation.ProfileDataFieldGroup;
import ru.systemoteh.resume.annotation.constraint.Adulthood;
import ru.systemoteh.resume.annotation.constraint.EnglishLanguage;
import ru.systemoteh.resume.annotation.constraint.Phone;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "profile")
@Getter
@Setter
@Document(indexName="profile")
public class Profile extends AbstractEntity<Long> implements Serializable {

    @Id
    @SequenceGenerator(name = "PROFILE_ID_GENERATOR", sequenceName = "PROFILE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROFILE_ID_GENERATOR")
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(name = "uid", nullable = false, length = 100)
    private String uid;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @JsonIgnore
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Email
    @NotNull
    @JsonIgnore
    @Size(max = 100)
    @Column(name = "email", nullable = true, length = 100)
    @ProfileDataFieldGroup
    private String email;

    @Phone
    @NotNull
    @JsonIgnore
    @Size(max = 20)
    @Column(name = "phone", nullable = true, length = 20)
    @ProfileDataFieldGroup
    private String phone;

    @Adulthood
    @NotNull
    @Column(name = "birth_day", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    @ProfileDataFieldGroup
    private Date birthDay;

    @NotNull
    @Size(max = 100)
    @SafeHtml
    @EnglishLanguage(withNumbers = false, withSpecialSymbols = false)
    @Column(name = "country", nullable = true, length = 100)
    @ProfileDataFieldGroup
    private String country;

    @NotNull
    @Size(max = 100)
    @SafeHtml
    @EnglishLanguage(withNumbers = false, withSpecialSymbols = false)
    @Column(name = "city", nullable = true, length = 100)
    @ProfileDataFieldGroup
    private String city;

    @NotNull
    @SafeHtml
    @EnglishLanguage
    @Column(name = "objective", nullable = true, length = -1)
    @ProfileDataFieldGroup
    private String objective;

    @NotNull
    @SafeHtml
    @EnglishLanguage
    @Column(name = "summary", nullable = true, length = -1)
    @ProfileDataFieldGroup
    private String summary;

    @JsonIgnore
    @Size(max = 255)
    @Column(name = "large_photo", nullable = true, length = 255)
    private String largePhoto;

    @Size(max = 255)
    @Column(name = "small_photo", nullable = true, length = 255)
    private String smallPhoto;

    @Column(name = "info", nullable = true, length = -1)
    private String info;

    @Column(name = "created", insertable = false, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @JsonIgnore
    @Column(name = "completed", nullable = false)
    private Boolean completed;

    @JsonIgnore
    @Embedded
    private Contact contacts;

    @OneToMany(mappedBy = "profile"/*, cascade={CascadeType.MERGE, CascadeType.PERSIST}*/)
    private Collection<Certificate> certificates;

    @OneToMany(mappedBy = "profile"/*, cascade={CascadeType.MERGE, CascadeType.PERSIST}*/)
    @OrderBy("id ASC")
    private Collection<Course> courses;

    @JsonIgnore
    @OneToMany(mappedBy = "profile"/*, cascade={CascadeType.MERGE, CascadeType.PERSIST}*/)
    @OrderBy("id ASC")
    private Collection<Education> educations;

    @JsonIgnore
    @OneToMany(mappedBy = "profile"/*, cascade={CascadeType.MERGE, CascadeType.PERSIST}*/)
    @OrderBy("id ASC")
    private Collection<Hobby> hobbies;

    @OneToMany(mappedBy = "profile"/*, cascade={CascadeType.MERGE, CascadeType.PERSIST}*/)
    @OrderBy("id ASC")
    private Collection<Language> languages;

    @OneToMany(mappedBy = "profile"/*, cascade={CascadeType.MERGE, CascadeType.PERSIST}*/)
    @OrderBy("id ASC")
    private Collection<Practice> practices;

    @OneToMany(mappedBy = "profile"/*, cascade={CascadeType.MERGE, CascadeType.PERSIST}*/)
    @OrderBy("id ASC")
    private Collection<Skill> skills;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public int getAge() {
        LocalDate birthdate = new LocalDate(birthDay);
        LocalDate now = new LocalDate();
        Years age = Years.yearsBetween(birthdate, now);
        return age.getYears();
    }

    public String getProfilePhoto() {
        if (largePhoto != null) {
            return largePhoto;
        } else {
            return "/static/img/profile-placeholder.png";
        }
    }

    public void updateProfilePhotos(String largePhoto, String smallPhoto) {
        setLargePhoto(largePhoto);
        setSmallPhoto(smallPhoto);
    }

    public Contact getContacts() {
        if (contacts == null) {
            contacts = new Contact();
        }
        return contacts;
    }
}
