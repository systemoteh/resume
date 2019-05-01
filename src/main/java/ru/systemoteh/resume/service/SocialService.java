package ru.systemoteh.resume.service;

import ru.systemoteh.resume.domain.Profile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface SocialService<T> {

    @Nullable
    Profile login(@Nonnull T model);

    @Nullable
    Profile createNewProfile(@Nonnull T model);
}
