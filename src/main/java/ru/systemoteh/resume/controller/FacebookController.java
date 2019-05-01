package ru.systemoteh.resume.controller;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.scope.ExtendedPermissions;
import com.restfb.scope.ScopeBuilder;
import com.restfb.scope.UserDataPermissions;
import com.restfb.types.User;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.systemoteh.resume.domain.Profile;
import ru.systemoteh.resume.service.SocialService;
import ru.systemoteh.resume.util.SecurityUtil;

@Controller
public class FacebookController {

    @Value("${social.facebook.idClient}")
    private String idClient;

    @Value("${social.facebook.secret}")
    private String secret;

    @Autowired
    private SocialService<User> facebookSocialService;

    private String redirectUrl;

    @Value("${application.host}")
    public void setRedirectUrl(String appHost) {
        this.redirectUrl = appHost + "/fromFb";
    }

    private String getAuthorizeUrl() {
        ScopeBuilder scopeBuilder = new ScopeBuilder().addPermission(ExtendedPermissions.EMAIL)
                .addPermission(UserDataPermissions.USER_BIRTHDAY).addPermission(UserDataPermissions.USER_HOMETOWN)
                .addPermission(UserDataPermissions.USER_LOCATION);
        FacebookClient client = new DefaultFacebookClient(Version.VERSION_2_6);
        return client.getLoginDialogUrl(idClient, redirectUrl, scopeBuilder);
    }

    @RequestMapping(value = {"/fbLogin", "fbSignUp"}, method = RequestMethod.GET)
    public String gotoFacebook() {
        return "redirect:" + getAuthorizeUrl();
    }

    @RequestMapping(value = {"/fromFb"}, method = RequestMethod.GET)
    public String fromFb(@RequestParam(value = "code", required = false) String code) {
        if (StringUtils.isBlank(code)) {
            return "redirect:/sign-in";
        } else {
            User user = fetchMe(code);
            return processFacebookUser(user);
        }
    }

    protected User fetchMe(String code) {
        FacebookClient client = new DefaultFacebookClient(Version.VERSION_2_6);
        FacebookClient.AccessToken accessToken = client.obtainUserAccessToken(idClient, secret, redirectUrl, code);
        client = new DefaultFacebookClient(accessToken.getAccessToken(), Version.VERSION_2_6);
        User user = client.fetchObject("me", User.class,
                Parameter.with("fields", "id,email,first_name,last_name,birthday,hometown,location"));
        return user;
    }

    protected String processFacebookUser(User user) {
        Profile profile = facebookSocialService.login(user);
        if (profile == null) {
            profile = facebookSocialService.createNewProfile(user);
            if (profile == null) {
                return "redirect:/sign-in";
            }
        }
        SecurityUtil.authenticateWithRememberMe(profile);
        return "redirect:/" + profile.getUid();
    }
}
