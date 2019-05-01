package ru.systemoteh.resume.service;

import ru.systemoteh.resume.model.NotificationMessage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface NotificationTemplateService {

    @Nonnull
    NotificationMessage createNotificationMessage(@Nonnull String templateName, @Nullable Object model);
}