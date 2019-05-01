package ru.systemoteh.resume.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.SafeHtml;
import ru.systemoteh.resume.annotation.constraint.EnglishLanguage;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode(exclude = "profile")
@Entity
@Table(name = "skill")
public class Skill extends AbstractEntity<Long> implements ProfileCollectionField, Serializable, Comparable<Skill> {

    @Id
    @SequenceGenerator(name = "SKILL_ID_GENERATOR", sequenceName = "SKILL_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SKILL_ID_GENERATOR")
    @Column(unique = true, nullable = false)
    private Long id;

    @SafeHtml
    @EnglishLanguage
    @Column(name = "name", nullable = false, length = 2147483647)
    private String name;

    @Column(name = "skill_category_id", nullable = false)
    private Long skillCategoryId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", referencedColumnName = "id", nullable = false)
    private Profile profile;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_category_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private SkillCategory skillCategory;

    @Override
    public int compareTo(Skill o) {
        return getSkillCategoryId().compareTo(o.getSkillCategoryId());
    }
}
