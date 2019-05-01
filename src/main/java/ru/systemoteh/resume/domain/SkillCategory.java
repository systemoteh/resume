package ru.systemoteh.resume.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "skill_category", schema = "public", catalog = "resume")
public class SkillCategory extends AbstractEntity<Long> {

    @Id
    @SequenceGenerator(name = "SKILL_CATEGORY_ID_GENERATOR", sequenceName = "SKILL_CATEGORY_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SKILL_CATEGORY_ID_GENERATOR")
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @OneToMany(mappedBy = "skillCategory")
    private Collection<Skill> skills;

    public SkillCategory(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}
