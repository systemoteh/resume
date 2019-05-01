package ru.systemoteh.resume.service.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.systemoteh.resume.domain.Profile;
import ru.systemoteh.resume.model.NotificationMessage;
import ru.systemoteh.resume.service.NotificationManagerService;
import ru.systemoteh.resume.service.NotificationSenderService;
import ru.systemoteh.resume.service.NotificationTemplateService;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationManagerServiceImpl implements NotificationManagerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationManagerServiceImpl.class);

    @Autowired
    private NotificationSenderService notificationSenderService;

    @Autowired
    private NotificationTemplateService notificationTemplateService;

    @Override
    public void sendPasswordChanged(@Nonnull Profile profile) {
        LOGGER.debug("Password changed for account {}", profile.getUid());
        processNotification(profile, "passwordChangedNotification", buildNewModelWithProfile(profile));
    }

    @Override
    public void sendPasswordGenerated(Profile profile, String generatedPassword) {
        LOGGER.debug("Password generated for account {}", profile.getUid());
        Map<String, Object> model = buildNewModelWithProfile(profile);
        model.put("generatedPassword", generatedPassword);
        processNotification(profile, "passwordGeneratedNotification", model);
    }

    @Override
    public void sendRestoreAccessLink(Profile profile, String restoreLink) {
        LOGGER.debug("Restore link: {} for account {}", restoreLink, profile.getUid());
        Map<String, Object> model = buildNewModelWithProfile(profile);
        model.put("restoreLink", restoreLink);
        processNotification(profile, "restoreAccessNotification", model);
    }

    protected void processNotification(Profile profile, String templateName, Object model) {
        String destinationAddress = notificationSenderService.findDestinationAddress(profile);
        if (StringUtils.isNotBlank(destinationAddress)) {
            NotificationMessage notificationMessage = notificationTemplateService.createNotificationMessage(templateName, model);
            notificationMessage.setDestinationAddress(destinationAddress);
            notificationMessage.setDestinationName(profile.getFullName());
            notificationSenderService.sendNotification(notificationMessage);
        } else {
            LOGGER.error("Notification ignored: destinationAddress is empty for profile " + profile.getUid());
        }
    }

    protected Map<String, Object> buildNewModelWithProfile(Profile profile) {
        Map<String, Object> model = new HashMap<>();
        model.put("profile", profile);
        return model;
    }
}
