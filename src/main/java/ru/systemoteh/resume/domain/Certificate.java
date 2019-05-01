package ru.systemoteh.resume.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.SafeHtml;
import ru.systemoteh.resume.annotation.constraint.EnglishLanguage;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "certificate")
public class Certificate extends AbstractEntity<Long> implements ProfileCollectionField {

    @Id
    @SequenceGenerator(name = "CERTIFICATE_ID_GENERATOR", sequenceName = "CERTIFICATE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CERTIFICATE_ID_GENERATOR")
    @Column(unique = true, nullable = false)
    private Long id;

    @SafeHtml
    @EnglishLanguage
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @JsonIgnore
    @Column(name = "large_url", nullable = false, length = 255)
    private String largeUrl;

    @JsonIgnore
    @Column(name = "small_url", nullable = false, length = 255)
    private String smallUrl;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", referencedColumnName = "id", nullable = false)
    private Profile profile;

}