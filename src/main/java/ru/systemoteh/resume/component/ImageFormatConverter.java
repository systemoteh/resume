package ru.systemoteh.resume.component;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;

public interface ImageFormatConverter {

    void convert(@Nonnull Path sourceImageFile, @Nonnull Path destImageFile) throws IOException;
}