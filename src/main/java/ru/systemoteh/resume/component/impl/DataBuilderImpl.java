package ru.systemoteh.resume.component.impl;

import org.apache.commons.lang.WordUtils;
import org.springframework.stereotype.Component;
import ru.systemoteh.resume.component.DataBuilder;
import ru.systemoteh.resume.util.DataUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Component
public class DataBuilderImpl implements DataBuilder {

    private static final String UID_DELIMETER = "-";

    @Nonnull
    @Override
    public String buildCertificateName(@Nullable String fileName) {
        if (fileName == null) {
            return "";
        }
        int point = fileName.lastIndexOf('.');
        if (point != -1) {
            fileName = fileName.substring(0, point);
        }
        return WordUtils.capitalize(fileName);
    }

    @Override
    public String buildProfileUid(String firstName, String lastName) {
        return DataUtil.normalizeName(firstName) + UID_DELIMETER + DataUtil.normalizeName(lastName);
    }

    @Override
    public String rebuildUidWithRandomSuffix(String baseUid, String alphabet, int letterCount) {
        return baseUid + UID_DELIMETER + DataUtil.generateRandomString(alphabet, letterCount);
    }

    @Override
    public String buildRestoreAccessLink(String appHost, String token) {
        return appHost + "/restore/" + token;
    }
}
