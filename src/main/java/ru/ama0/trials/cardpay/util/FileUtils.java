package ru.ama0.trials.cardpay.util;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class FileUtils {
    public static String getFileExtension(File file) {
        if (file == null) {
            return "";
        }
        String name = file.getName();
        int i = name.lastIndexOf('.');
        return i > 0 ? name.substring(i + 1) : "";
    }

    public static boolean fileExists(File file) {
        return file.exists() && !file.isDirectory();
    }

    public static Set<File> convertAndCheckFilesExist(String... fileNames) {
        return Arrays.stream(fileNames)
                .distinct()
                .map(File::new)
                .peek(file -> {
                    if (!FileUtils.fileExists(file)) {
                        throw new IllegalArgumentException(String.format("Input filename (%s) does not exist.",
                                file.toPath().toAbsolutePath()));
                    }
                })
                .collect(Collectors.toSet());
    }
}
