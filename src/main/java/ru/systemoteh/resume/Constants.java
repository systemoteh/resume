package ru.systemoteh.resume;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class Constants {

    public static final int MAX_PROFILES_PER_PAGE = 10;

    public static final String USER = "USER";

    public static final String[] EMPTY_ARRAY = {};

    @Getter
    @AllArgsConstructor
    public static enum UIImageType {

        AVATAR(110, 110, 400, 400),

        CERTIFICATE(142, 100, 900, 700);

        private final int smallWidth;
        private final int smallHeight;
        private final int largeWidth;
        private final int largeHeight;

        public String getFolderName() {
            return name().toLowerCase();
        }
    }

}
