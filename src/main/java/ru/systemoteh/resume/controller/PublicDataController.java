package ru.systemoteh.resume.controller;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.systemoteh.resume.annotation.constraint.FieldMatch;
import ru.systemoteh.resume.component.FormErrorConverter;
import ru.systemoteh.resume.domain.Profile;
import ru.systemoteh.resume.form.SignUpForm;
import ru.systemoteh.resume.model.CurrentProfile;
import ru.systemoteh.resume.service.EditProfileService;
import ru.systemoteh.resume.service.FindProfileService;
import ru.systemoteh.resume.util.SecurityUtil;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static ru.systemoteh.resume.Constants.MAX_PROFILES_PER_PAGE;

@Controller
public class PublicDataController {

    @Autowired
    FindProfileService findProfileService;

    @Autowired
    private EditProfileService editProfileService;

    @Autowired
    private FormErrorConverter formErrorConverter;


    @RequestMapping(value = {"/welcome"})
    public String welcome(Model model) {
        return "welcome";
    }

    @RequestMapping(value = {"/profiles"})
    public String profiles(Model model) {
        Page<Profile> profiles = findProfileService.findAll(new PageRequest(0, MAX_PROFILES_PER_PAGE, new Sort("id")));
        model.addAttribute("profiles", profiles.getContent());
        model.addAttribute("page", profiles);
        return "profiles";
    }

    @RequestMapping(value = "/fragment/more", method = RequestMethod.GET)
    public String moreProfiles(Model model, @RequestParam(value = "query", required = false) String query,
                               @PageableDefault(size = MAX_PROFILES_PER_PAGE) @SortDefault(sort = "id") Pageable pageable)
            throws UnsupportedEncodingException {
        Page<Profile> profiles;
        if (StringUtils.isBlank(query)) {
            profiles = findProfileService.findAll(pageable);
        } else if (query.startsWith("~")) {
            profiles = findProfileService.findByFullContextSearchQuery(query.substring(1), pageable);
        } else {
            profiles = findProfileService.findByStrictSearchQuery(query, pageable);
        }
        model.addAttribute("profiles", profiles.getContent());
        return "fragment/profile-items";
    }

    @RequestMapping(value = "/{uid}")
    public String profile(@PathVariable String uid, Model model) {
        Profile profile = findProfileService.findByUid(uid);
        if (profile == null) {
            return "profile-not-found";
        } else if (!profile.getCompleted()) {
            CurrentProfile currentProfile = SecurityUtil.getCurrentProfile();
            if (currentProfile == null || !currentProfile.getId().equals(profile.getId())) {
                return "profile-not-found";
            } else {
                return "redirect:/edit";
            }
        } else {
            model.addAttribute("profile", profile);
            return "profile";
        }
    }

    @RequestMapping(value = "/sign-in")
    public String signIn() {
        return "sign-in";
    }

    @RequestMapping(value = "/sign-in-failed")
    public String signInFailed(HttpSession session) {
        if (session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION") == null) {
            return "redirect:/sign-in";
        } else {
            return "sign-in";
        }
    }

    @RequestMapping(value = "/sign-up", method = RequestMethod.GET)
    public String signUp(Model model) {
        model.addAttribute("profileForm", new SignUpForm());
        return "sign-up";
    }

    @RequestMapping(value = "/sign-up", method = RequestMethod.POST)
    public String signUp(@Valid @ModelAttribute("profileForm") SignUpForm signUpForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            formErrorConverter.convertToFieldError(FieldMatch.class, signUpForm, bindingResult);
            return "sign-up";
        } else {
            Profile profile = editProfileService.createNewProfile(signUpForm);
            SecurityUtil.authenticateWithRememberMe(profile);
            return "redirect:/sign-up/success";
        }
    }

    @RequestMapping(value = "/sign-up/success", method = RequestMethod.GET)
    public String signUpSuccess() {
        return "sign-up-success";
    }

    @RequestMapping(value = "/restore", method = RequestMethod.GET)
    public String getRestoreAccess() {
        return "restore";
    }

    @RequestMapping(value = "/restore/success", method = RequestMethod.GET)
    public String getRestoreSuccess() {
        return "restore-success";
    }

    @RequestMapping(value = "/restore", method = RequestMethod.POST)
    public String processRestoreAccess(@RequestParam("uid") String anyUniqueId) {
        findProfileService.restoreAccess(anyUniqueId);
        return "redirect:/restore/success";
    }

    @RequestMapping(value = "/restore/{token}", method = RequestMethod.GET)
    public String restoreAccess(@PathVariable("token") String token) {
        Profile profile = findProfileService.findByRestoreToken(token);
        SecurityUtil.authenticate(profile);
        return "redirect:/edit/password";
    }

    @RequestMapping(value = "/error")
    public String error() {
        return "error";
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String searchProfiles(@RequestParam(value = "query", required = false) String query, Model model,
                                 @PageableDefault(size = MAX_PROFILES_PER_PAGE)
                                 @SortDefault(sort = "id") Pageable pageable)
            throws UnsupportedEncodingException {
        Page<Profile> profiles;
        if (StringUtils.isBlank(query)) {
            return "redirect:/profiles";
        } else if (query.startsWith("~")){
            profiles = findProfileService.findByFullContextSearchQuery(query.substring(1), pageable);
        } else {
            profiles = findProfileService.findByStrictSearchQuery(query, pageable);
        }
        model.addAttribute("profiles", profiles.getContent());
        model.addAttribute("page", profiles);
        model.addAttribute("query", URLDecoder.decode(query, "UTF-8"));
        return "search-results";
    }

}
