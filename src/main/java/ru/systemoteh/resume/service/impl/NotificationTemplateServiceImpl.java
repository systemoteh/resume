package ru.systemoteh.resume.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.ServletContextResource;
import ru.systemoteh.resume.component.TemplateResolver;
import ru.systemoteh.resume.exception.CantCompleteClientRequestException;
import ru.systemoteh.resume.model.NotificationMessage;
import ru.systemoteh.resume.service.NotificationTemplateService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class NotificationTemplateServiceImpl implements NotificationTemplateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationTemplateServiceImpl.class);

    private Map<String, NotificationMessage> notificationTemplates;

    @Value("${notification.config.path}")
    private String notificationConfigPath;

    @Autowired(required = false)
    private ServletContext servletContext;

    @Autowired
    private TemplateResolver templateResolver;

    @PostConstruct
    private void postConstruct() throws IOException {
        notificationTemplates = Collections.unmodifiableMap(getNotificationTemplates());
        LOGGER.info("Loaded {} notification templates", notificationTemplates.size());
    }


    @Nonnull
    @Override
    public NotificationMessage createNotificationMessage(@Nonnull String templateName, @Nullable Object model) {
        NotificationMessage message = notificationTemplates.get(templateName);
        if (message == null) {
            throw new CantCompleteClientRequestException("Notification template '" + templateName + "' not found");
        }
        String resolvedSubject = templateResolver.resolve(message.getSubject(), model);
        String resolvedContent = templateResolver.resolve(message.getContent(), model);
        return new NotificationMessage(resolvedSubject, resolvedContent);
    }

    protected Map<String, NotificationMessage> getNotificationTemplates() throws IOException {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        reader.setValidating(false);
        reader.loadBeanDefinitions(getResource());
        return beanFactory.getBeansOfType(NotificationMessage.class);
    }

    protected Resource getResource() throws IOException {
        for (Resource resource : getResourceCandidates()) {
            if (resource.exists()) {
                return resource;
            }
        }
        throw new IOException("Resource " + notificationConfigPath + " not found");
    }

    protected Iterable<Resource> getResourceCandidates() {
        List<Resource> resourceCandidates = new ArrayList<>();
        resourceCandidates.add(new ServletContextResource(servletContext, notificationConfigPath));
        resourceCandidates.add(new PathResource(notificationConfigPath));
        resourceCandidates.add(new ClassPathResource(notificationConfigPath));
        try {
            resourceCandidates.add(new UrlResource(notificationConfigPath));
        } catch (MalformedURLException e) {
            //ignore error
        }
        return resourceCandidates;
    }

}
