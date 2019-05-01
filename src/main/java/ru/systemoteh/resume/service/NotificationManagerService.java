package ru.systemoteh.resume.service;

import ru.systemoteh.resume.domain.Profile;

import javax.annotation.Nonnull;

public interface NotificationManagerService {

    void sendPasswordChanged(@Nonnull Profile profile);

    void sendPasswordGenerated(@Nonnull Profile profile, @Nonnull String generatedPassword);

    void sendRestoreAccessLink(@Nonnull Profile profile, @Nonnull String restoreLink);

}
