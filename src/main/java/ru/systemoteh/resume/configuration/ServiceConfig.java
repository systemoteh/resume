package ru.systemoteh.resume.configuration;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan({"ru.systemoteh.resume.service.impl", "ru.systemoteh.resume.component.impl"})
public class ServiceConfig {

    /**
     * http://docs.spring.io/autorepo/docs/spring/4.2.5.RELEASE/spring-framework-reference/html/beans.html
     * <p>
     * Also, be particularly careful with BeanPostProcessor and BeanFactoryPostProcessor definitions via @Bean.
     * Those should usually be declared as static @Bean methods, not triggering the instantiation of their containing configuration class.
     * Otherwise, @Autowired and @Value won`t work on the configuration class itself since it is being created as a bean instance too early.
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setLocations(
                new ClassPathResource("/properties/application.properties"),
                new ClassPathResource("/properties/logic.properties"),
                new ClassPathResource("/properties/elasticsearch.properties")
        );
        return configurer;
    }

    @Bean
    public PropertiesFactoryBean properties() {
        PropertiesFactoryBean properties = new PropertiesFactoryBean();
        properties.setLocation(new ClassPathResource("/properties/logic.properties"));
        return properties;
    }

}
