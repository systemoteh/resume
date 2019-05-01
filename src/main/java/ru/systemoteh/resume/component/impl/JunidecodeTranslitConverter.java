package ru.systemoteh.resume.component.impl;

import net.sf.junidecode.Junidecode;
import org.springframework.stereotype.Component;
import ru.systemoteh.resume.component.TranslitConverter;

@Component
public class JunidecodeTranslitConverter implements TranslitConverter {

    @Override
    public String translit(String text) {
        return Junidecode.unidecode(text);
    }
}
