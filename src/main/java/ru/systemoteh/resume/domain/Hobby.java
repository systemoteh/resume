package ru.systemoteh.resume.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "name")
@Entity
@Table(name = "hobby")
public class Hobby extends AbstractEntity<Long> implements ProfileCollectionField, Serializable, Comparable<Hobby> {

    @Id
    @SequenceGenerator(name = "HOBBY_ID_GENERATOR", sequenceName = "HOBBY_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HOBBY_ID_GENERATOR")
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", referencedColumnName = "id", nullable = false)
    private Profile profile;

    @Transient
    private boolean selected;

    public Hobby(String name) {
        super();
        this.name = name;
    }

    public Hobby(String name, boolean selected) {
        super();
        this.name = name;
        this.selected = selected;
    }

    @Transient
    public String getCssClassName() {
        return name.replace(" ", "-").toLowerCase();
    }

    @Override
    public int compareTo(Hobby o) {
        return getName().compareTo(o.getName());
    }
}
