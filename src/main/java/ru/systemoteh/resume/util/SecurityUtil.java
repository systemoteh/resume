package ru.systemoteh.resume.util;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.systemoteh.resume.domain.Profile;
import ru.systemoteh.resume.model.CurrentProfile;
import ru.systemoteh.resume.model.CurrentProfileImpl;
import ru.systemoteh.resume.service.impl.RememberMeService;

import javax.annotation.Nullable;
import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unchecked")
public final class SecurityUtil {

    public static @Nullable
    CurrentProfile getCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof CurrentProfile) {
            return ((CurrentProfile) principal);
        } else {
            return null;
        }
    }

    public static Authentication authenticate(Profile profile) {
        CurrentProfileImpl currentProfile = new CurrentProfileImpl(profile);
        Authentication authentication = new UsernamePasswordAuthenticationToken(currentProfile, currentProfile.getPassword(), currentProfile.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    public static void authenticateWithRememberMe(Profile profile) {
        Authentication authentication = authenticate(profile);
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        WebApplicationContext ctx = (WebApplicationContext) request.getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        RememberMeService rememberMeService = ctx.getBean(RememberMeService.class);
        rememberMeService.createAutoLoginToken(request, requestAttributes.getResponse(), authentication);
    }

    public static String generateNewRestoreAccessToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static void logout(FilterChainProxy springSecurityFilterChain) {
        List<LogoutHandler> handlers = getLogoutHandlers(springSecurityFilterChain);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        for (LogoutHandler handler : handlers) {
            handler.logout(requestAttributes.getRequest(), requestAttributes.getResponse(), auth);
        }
    }

    private static List<LogoutHandler> getLogoutHandlers(FilterChainProxy springSecurityFilterChain) {
        LogoutFilter logoutFilter = findLogoutFilter(springSecurityFilterChain);
        Field handlers = ReflectionUtils.findField(LogoutFilter.class, "handlers");
        handlers.setAccessible(true);
        return (List<LogoutHandler>) ReflectionUtils.getField(handlers, logoutFilter);
    }

    private static LogoutFilter findLogoutFilter(FilterChainProxy springSecurityFilterChain) {
        List<Filter> filters = springSecurityFilterChain.getFilters("/");
        for (Filter f : filters) {
            if (f instanceof LogoutFilter) {
                return (LogoutFilter) f;
            }
        }
        throw new IllegalStateException("Logout filter not found in filters: " + filters);
    }

}
