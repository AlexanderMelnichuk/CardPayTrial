package ru.ama0.trials.cardpay.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
@Slf4j
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
        return (file != null) && file.exists() && !file.isDirectory();
    }

    public static Set<File> convertAndCheckFilesExist(String... fileNames) {
        return Arrays.stream(fileNames)
                .distinct()
                .map(File::new)
                .filter(file -> {
                    if (FileUtils.fileExists(file)) {
                        return true;
                    } else {
                        log.error("Input filename '{}' does not exist.",
                                file.toPath().toAbsolutePath());
                        return false;
                    }
                })
                .collect(Collectors.toSet());
    }
}
