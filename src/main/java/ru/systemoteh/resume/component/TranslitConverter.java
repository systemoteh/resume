package ru.systemoteh.resume.component;

import javax.annotation.Nonnull;

public interface TranslitConverter {

    @Nonnull
    String translit(@Nonnull String text);
}
