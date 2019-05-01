package ru.systemoteh.resume.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.SafeHtml;
import ru.systemoteh.resume.annotation.constraint.EnglishLanguage;
import ru.systemoteh.resume.model.LanguageLevel;
import ru.systemoteh.resume.model.LanguageType;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode(exclude = "profile")
@Entity
@Table(name = "language")
public class Language extends AbstractEntity<Long> implements ProfileCollectionField, Serializable, Comparable<Language> {

    @Id
    @SequenceGenerator(name = "LANGUAGE_ID_GENERATOR", sequenceName = "LANGUAGE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LANGUAGE_ID_GENERATOR")
    @Column(unique = true, nullable = false)
    private Long id;

    @SafeHtml
    @EnglishLanguage(withSpecialSymbols = false, withNumbers = false, withPunctuations = false)
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @JsonIgnore
    @Column(nullable = false)
    @Convert(converter = LanguageLevel.PersistJPAConverter.class)
    private LanguageLevel level;

    @Column
    @Convert(converter = LanguageType.PersistJPAConverter.class)
    private LanguageType type;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @Transient
    public boolean isHasLanguageType() {
        return type != LanguageType.ALL;
    }

    @Override
    public int compareTo(Language o) {
        return getName().compareToIgnoreCase(o.getName());
    }
}
