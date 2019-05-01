package ru.systemoteh.resume.service;

import ru.systemoteh.resume.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;

public interface ImageStorageService {

    void save(@Nonnull String imageLink, @Nonnull Path tempImageFile);

    @Nonnull
    String createImageLink(@Nonnull String imageName, @Nonnull Constants.UIImageType imageType);

    void remove (@Nullable String ... imageLinks);

}
