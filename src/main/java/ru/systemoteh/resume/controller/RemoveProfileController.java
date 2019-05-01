package ru.systemoteh.resume.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.systemoteh.resume.service.EditProfileService;
import ru.systemoteh.resume.util.SecurityUtil;

import javax.servlet.Filter;

@Controller
public class RemoveProfileController {
    @Autowired
    private EditProfileService editProfileService;

    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    public void setSpringSecurityFilterChain(Filter springSecurityFilterChain) {
        this.springSecurityFilterChain = (FilterChainProxy) springSecurityFilterChain;
    }

    @RequestMapping(value = "/remove", method = RequestMethod.GET)
    public String showRemovePage() {
        return "remove";
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public String removeProfile() {
        editProfileService.removeProfile(SecurityUtil.getCurrentProfile());
        SecurityUtil.logout(springSecurityFilterChain);
        return "redirect:/welcome";
    }
}
