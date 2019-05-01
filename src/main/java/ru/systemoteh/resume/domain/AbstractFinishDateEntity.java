package ru.systemoteh.resume.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractFinishDateEntity<T> extends AbstractEntity<T> {

    @JsonIgnore
    @Column(name = "finish_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date finishDate;

    @JsonIgnore
    @Transient
    private Integer finishDateMonth;

    @JsonIgnore
    @Transient
    private Integer finishDateYear;


    @Transient
    public boolean isFinish() {
        return finishDate != null;
    }

    @Transient
    public Integer getFinishDateMonth() {
        if (finishDate != null) {
            return new DateTime(finishDate).getMonthOfYear();
        } else {
            return null;
        }
    }

    public void setFinishDateMonth(Integer finishDateMonth) {
        this.finishDateMonth = finishDateMonth;
        setupFinishDate();
    }

    @Transient
    public Integer getFinishDateYear() {
        if (finishDate != null) {
            return new DateTime(finishDate).getYear();
        } else {
            return null;
        }
    }

    public void setFinishDateYear(Integer finishDateYear) {
        this.finishDateYear = finishDateYear;
        setupFinishDate();
    }

    private void setupFinishDate() {
        if (finishDateYear != null && finishDateMonth != null) {
            setFinishDate(new Date(new DateTime(finishDateYear, finishDateMonth, 1, 0, 0).getMillis()));
        } else {
            setFinishDate(null);
        }
    }

}
