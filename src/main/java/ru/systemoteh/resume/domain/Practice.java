package ru.systemoteh.resume.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.SafeHtml;
import org.joda.time.DateTime;
import ru.systemoteh.resume.annotation.EnableFormErrorConversion;
import ru.systemoteh.resume.annotation.constraint.EnglishLanguage;
import ru.systemoteh.resume.annotation.constraint.FirstFieldLessThanSecond;
import ru.systemoteh.resume.util.DataUtil;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode(exclude = "profile")
@FirstFieldLessThanSecond(first = "beginDate", second = "finishDate")
@EnableFormErrorConversion(formName="practiceForm", fieldReference="finishDate", validationAnnotationClass= FirstFieldLessThanSecond.class)
@Entity
@Table(name = "practice")
public class Practice extends AbstractFinishDateEntity<Long> implements ProfileCollectionField, Serializable, Comparable<Practice> {

    @Id
    @SequenceGenerator(name = "PRACTICE_ID_GENERATOR", sequenceName = "PRACTICE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PRACTICE_ID_GENERATOR")
    @Column(unique = true, nullable = false)
    private Long id;

    @SafeHtml
    @EnglishLanguage(withSpecialSymbols = false)
    @Column(name = "position", nullable = false, length = 100)
    private String position;

    @SafeHtml
    @EnglishLanguage(withSpecialSymbols = false)
    @Column(name = "company", nullable = false, length = 100)
    private String company;

    @JsonIgnore
    @Column(name = "begin_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date beginDate;

    @SafeHtml
    @EnglishLanguage(withSpecialSymbols = false)
    @Column(name = "responsibilities", nullable = false, length = -1)
    private String responsibilities;

    @JsonIgnore
    @Column(name = "demo_url", nullable = true, length = 255)
    private String demoUrl;

    @JsonIgnore
    @Column(name = "src_url", nullable = true, length = 255)
    private String srcUrl;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", referencedColumnName = "id", nullable = false)
    private Profile profile;

    @Transient
    @JsonIgnore
    private Integer beginDateMonth;

    @Transient
    @JsonIgnore
    private Integer beginDateYear;

    @Transient
    public Integer getBeginDateMonth() {
        if (beginDate != null) {
            return new DateTime(beginDate).getMonthOfYear();
        } else {
            return null;
        }
    }

    public void setBeginDateMonth(Integer beginDateMonth) {
        this.beginDateMonth = beginDateMonth;
        setupBeginDate();
    }

    @Transient
    public Integer getBeginDateYear() {
        if (beginDate != null) {
            return new DateTime(beginDate).getYear();
        } else {
            return null;
        }
    }

    public void setBeginDateYear(Integer beginDateYear) {
        this.beginDateYear = beginDateYear;
        setupBeginDate();
    }

    private void setupBeginDate() {
        if (beginDateYear != null && beginDateMonth != null) {
            setBeginDate(new Timestamp(new DateTime(beginDateYear, beginDateMonth, 1, 0, 0).getMillis()));
        } else {
            setBeginDate(null);
        }
    }

    @Override
    public int compareTo(Practice o) {
        int res = DataUtil.compareByFields(o.getFinishDate(), getFinishDate(), true);
        if (res == 0) {
            return DataUtil.compareByFields(o.getBeginDate(), getBeginDate(), true);
        } else {
            return res;
        }
    }
}
