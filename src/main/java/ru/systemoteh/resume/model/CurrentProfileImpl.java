package ru.systemoteh.resume.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import ru.systemoteh.resume.Constants;
import ru.systemoteh.resume.domain.Profile;

import java.util.Collections;

@Getter
@Setter
public class CurrentProfileImpl extends User implements CurrentProfile {

    private final Long id;

    private final String fullName;

    public CurrentProfileImpl(Profile profile) {
        super(profile.getUid(), profile.getPassword(), true, true, true, true,
                Collections.singleton(new SimpleGrantedAuthority(Constants.USER)));
        this.id = profile.getId();
        this.fullName = profile.getFullName();
    }

    @Override
    public String getUid() {
        return getUsername();
    }

}