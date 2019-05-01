package ru.systemoteh.resume.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
@EnableScheduling
public class SchedulingConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(applicationScheduler());
    }

    /**
     * http://docs.spring.io/autorepo/docs/spring/4.2.5.RELEASE/spring-framework-reference/html/beans.html
     * <p>
     * By default, beans defined using Java config that have a public close or shutdown method
     * are automatically enlisted with a destruction callback.
     */
    @Bean(/*destroyMethod="shutdown"*/)
    public ScheduledExecutorService applicationScheduler() {
        return Executors.newScheduledThreadPool(1);
    }
}
