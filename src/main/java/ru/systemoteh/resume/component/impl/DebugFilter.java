package ru.systemoteh.resume.component.impl;

import org.springframework.stereotype.Component;
import ru.systemoteh.resume.filter.AbstractFilter;
import ru.systemoteh.resume.util.DebugUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class DebugFilter extends AbstractFilter {

    public boolean isEnabledDebug() {
        return true;
    }

    public String[] getDebugUrl() {
        return new String[]{"/welcome", "/profiles", "/profile", "/my-profile", "/edit"};   // and everything you need for debug
    }

    @Override
    public void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws IOException, ServletException {
        try {
            LOGGER.info("**************************************************** start ***************************************************");
            DebugUtil.turnOnShowSQL();
            chain.doFilter(req, resp);
        } finally {
            DebugUtil.turnOffShowSQL();
            LOGGER.info("****************************************************  end  ***************************************************");
        }
    }
}
