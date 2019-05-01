package ru.systemoteh.resume.component.impl;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import ru.systemoteh.resume.component.ImageResizer;

import java.io.IOException;
import java.nio.file.Path;

@Component
public class ThumbnailsImageResizer implements ImageResizer {

    @Override
    public void resize(Path sourceImageFile, Path destImageFile, int width, int height) throws IOException {
        Thumbnails.of(sourceImageFile.toFile()).size(width, height).toFile(destImageFile.toFile());
    }
}