package ru.systemoteh.resume.component;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;

public interface ImageResizer {

    void resize(@Nonnull Path sourceImageFile, @Nonnull Path destImageFile, int width, int height) throws IOException;
}
