package ru.systemoteh.resume.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.systemoteh.resume.domain.Practice;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PracticeForm {

    @Valid
    private Collection<Practice> items = new ArrayList<>();
}
