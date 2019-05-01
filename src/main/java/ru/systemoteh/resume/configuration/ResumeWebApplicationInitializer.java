package ru.systemoteh.resume.configuration;

import org.sitemesh.builder.SiteMeshFilterBuilder;
import org.sitemesh.config.ConfigurableSiteMeshFilter;
import org.sitemesh.content.tagrules.html.Sm2TagRuleBundle;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.servlet.DispatcherServlet;
import ru.systemoteh.resume.component.impl.ApplicationListener;
import ru.systemoteh.resume.component.impl.DebugFilter;
import ru.systemoteh.resume.component.impl.ErrorHandler;

import javax.servlet.*;
import java.util.EnumSet;

public class ResumeWebApplicationInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        WebApplicationContext applicationContext = createWebApplicationContext(servletContext);

        servletContext.setSessionTrackingModes(EnumSet.of(SessionTrackingMode.COOKIE));
        servletContext.addListener(new ContextLoaderListener(applicationContext));
        servletContext.addListener(applicationContext.getBean(ApplicationListener.class));

        registerFilters(servletContext, applicationContext);
        registerSpringMvcDispatcherServlet(servletContext, applicationContext);
    }

    private WebApplicationContext createWebApplicationContext(ServletContext servletContext) {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.scan("ru.systemoteh.resume.configuration");
        context.setServletContext(servletContext);
        context.refresh();
        return context;
    }

    private void registerFilters(ServletContext servletContext, WebApplicationContext applicationContext) {
        registerFilter(servletContext, applicationContext.getBean(ErrorHandler.class));
        registerFilter(servletContext, new CharacterEncodingFilter("UTF-8", true));
        registerFilter(servletContext, new OpenEntityManagerInViewFilter());    // for initialization FetchType.LAZY fields
        registerFilter(servletContext, new RequestContextFilter());
        registerDebugFilterIfEnabled(servletContext, applicationContext.getBean(DebugFilter.class));
        registerFilter(servletContext, new DelegatingFilterProxy("springSecurityFilterChain", applicationContext), "springSecurityFilterChain");
        registerFilter(servletContext, createConfigurableSiteMeshFilter(), "sitemesh");
    }

    private void registerFilter(ServletContext servletContext, Filter filter, String... filterNames) {
        String filterName = filterNames.length > 0 ? filterNames[0] : filter.getClass().getSimpleName();
        servletContext.addFilter(filterName, filter).addMappingForUrlPatterns(null, true, "/*");
    }

    private void registerDebugFilterIfEnabled(ServletContext container, DebugFilter filter) {
        if (filter.isEnabledDebug() && filter.getDebugUrl().length != 0) {
            FilterRegistration.Dynamic filterRegistration = container.addFilter(filter.getClass().getSimpleName(), filter);
            for (String url : filter.getDebugUrl()) {
                filterRegistration.addMappingForUrlPatterns(null, true, url);
            }
        }
    }

    private ConfigurableSiteMeshFilter createConfigurableSiteMeshFilter() {
        return new ConfigurableSiteMeshFilter() {
            @Override
            protected void applyCustomConfiguration(SiteMeshFilterBuilder builder) {
                builder
                        .addDecoratorPath("/*", "/WEB-INF/template/page-template.jsp")
                        .addDecoratorPath("/fragment/*", "/WEB-INF/template/fragment-template.jsp")
                        .addTagRuleBundle(new Sm2TagRuleBundle());
            }
        };
    }

    private void registerSpringMvcDispatcherServlet(ServletContext servletContext, WebApplicationContext applicationContext) {
        ServletRegistration.Dynamic servletRegistration = servletContext.addServlet("dispatcher",
                new DispatcherServlet(applicationContext));
        servletRegistration.setLoadOnStartup(1);
        servletRegistration.addMapping("/");
    }
}
