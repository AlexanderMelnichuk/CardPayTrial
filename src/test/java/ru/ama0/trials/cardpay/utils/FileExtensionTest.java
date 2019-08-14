package ru.ama0.trials.cardpay.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)

public class FileExtensionTest {
    private File file;
    private String expectedExtension;

    public FileExtensionTest(File file, String expectedExtension) {
        this.file = file;
        this.expectedExtension = expectedExtension;
    }

    @Parameterized.Parameters
    public static Collection archivesToTest() {
        return Arrays.asList(new Object[][]{
                {null, ""},
                {new File("source2.zip"), "zip"},
                {new File("order.txt"), "txt"},
                {new File("order.json"), "json"},
                {new File("order"), ""}
        });
    }

    @Test
    public void getFileExtensionTest() {
        // Act
        String extension = FileUtils.getFileExtension(file);
        // Assert
        assertEquals(expectedExtension, extension);
    }
}
