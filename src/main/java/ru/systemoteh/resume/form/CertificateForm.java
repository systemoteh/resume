package ru.systemoteh.resume.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.systemoteh.resume.domain.Certificate;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CertificateForm {

    @Valid
    private Collection<Certificate> items = new ArrayList<>();
}
