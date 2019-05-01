package ru.systemoteh.resume.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.SafeHtml;
import ru.systemoteh.resume.annotation.constraint.EnglishLanguage;
import ru.systemoteh.resume.util.DataUtil;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "course")
@Getter
@Setter
@EqualsAndHashCode(exclude = "profile")
public class Course extends AbstractFinishDateEntity<Long> implements ProfileCollectionField, Serializable, Comparable<Course> {

    @Id
    @SequenceGenerator(name = "COURSE_ID_GENERATOR", sequenceName = "COURSE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COURSE_ID_GENERATOR")
    @Column(unique = true, nullable = false)
    private Long id;

    @EnglishLanguage(withSpecialSymbols = false)
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @SafeHtml
    @EnglishLanguage(withSpecialSymbols = false)
    @Column(name = "school", nullable = false, length = 100)
    private String school;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", referencedColumnName = "id", nullable = false)
    private Profile profile;

    @Override
    public int compareTo(Course o) {
        return DataUtil.compareByFields(o.getFinishDate(), getFinishDate(), true);
    }

}
