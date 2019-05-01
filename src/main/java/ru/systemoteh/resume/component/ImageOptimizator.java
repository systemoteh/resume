package ru.systemoteh.resume.component;

import javax.annotation.Nonnull;
import java.nio.file.Path;

public interface ImageOptimizator {

    void optimize(@Nonnull Path image);
}

