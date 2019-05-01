package ru.systemoteh.resume.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.systemoteh.resume.annotation.EnableFormErrorConversion;
import ru.systemoteh.resume.annotation.constraint.FirstFieldLessThanSecond;
import ru.systemoteh.resume.util.DataUtil;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode(exclude = "profile")
@FirstFieldLessThanSecond(first = "beginYear", second = "finishYear")
@EnableFormErrorConversion(formName = "educationForm", fieldReference = "finishYear", validationAnnotationClass = FirstFieldLessThanSecond.class)
@Entity
@Table(name = "education")
public class Education extends AbstractEntity<Long> implements ProfileCollectionField, Serializable, Comparable<Education> {

    @Id
    @SequenceGenerator(name = "EDUCATION_ID_GENERATOR", sequenceName = "EDUCATION_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EDUCATION_ID_GENERATOR")
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(name = "summary", nullable = false, length = 100)
    private String summary;

    @Column(name = "begin_year", nullable = false)
    private Integer beginYear;

    @Column(name = "finish_year", nullable = true)
    private Integer finishYear;

    @Column(name = "university", nullable = false, length = -1)
    private String university;

    @Column(name = "faculty", nullable = false, length = 255)
    private String faculty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", referencedColumnName = "id", nullable = false)
    private Profile profile;

    @Transient
    public boolean isFinish() {
        return finishYear != null;
    }

    @Override
    public int compareTo(Education o) {
        int res = DataUtil.compareByFields(o.getFinishYear(), getFinishYear(), true);
        if (res == 0) {
            return DataUtil.compareByFields(o.getBeginYear(), getBeginYear(), true);
        } else {
            return res;
        }
    }
}
