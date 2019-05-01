package ru.systemoteh.resume.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@PropertySource("classpath:properties/mail.properties")
public class EmailConfig {

    @Autowired
    private ConfigurableEnvironment environment;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(environment.getRequiredProperty("mail.smtp.host"));
        if (environment.containsProperty("mail.smtp.username")) {
            javaMailSender.setUsername(environment.resolveRequiredPlaceholders(environment.getRequiredProperty("mail.smtp.username")));
            javaMailSender.setPassword(environment.resolveRequiredPlaceholders(environment.getRequiredProperty("mail.smtp.password")));
            javaMailSender.setPort(Integer.parseInt(environment.getRequiredProperty("mail.smtp.port")));
            javaMailSender.setDefaultEncoding("UTF-8");
            javaMailSender.setJavaMailProperties(javaMailProperties());
        }
        return javaMailSender;
    }

    private Properties javaMailProperties() {
        Properties prop = new Properties();
        prop.setProperty("mail.smtp.auth", "true");
        prop.setProperty("mail.smtp.starttls.enable", "true");
        return prop;
    }
}
