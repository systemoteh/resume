package ru.systemoteh.resume.service;

import ru.systemoteh.resume.domain.Profile;
import ru.systemoteh.resume.model.NotificationMessage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface NotificationSenderService {

    void sendNotification(@Nonnull NotificationMessage message);

    @Nullable
    String findDestinationAddress(@Nonnull Profile profile);

}
