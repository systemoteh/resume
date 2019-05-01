package ru.systemoteh.resume.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode(exclude = "profile")
@Entity
@Table(name = "profile_restore", schema = "public", catalog = "resume")
public class ProfileRestore extends AbstractEntity<Long> implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "token", nullable = false, length = 255)
    private String token;

    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "id", nullable = false)
    private Profile profile;

}
