package ru.ama0.trials.cardpay.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)

public class FileExistsTest {
    private File file;
    private boolean expectedExistence;

    public FileExistsTest(File file, boolean expectedExistence) {
        this.file = file;
        this.expectedExistence = expectedExistence;
    }

    @Parameterized.Parameters
    public static Collection archivesToTest() {
        return Arrays.asList(new Object[][]{
                {null, false},
                {new File("src/test/resources/test.json"), true},
                {new File("src/test/resources/test.csv"), true},
                {new File("nonexistent-file.txt"), false},
                {new File("src/test/resources/"), false}
        });
    }

    @Test
    public void fileExistsTest() {
        // Act
        boolean existence = FileUtils.fileExists(file);
        // Assert
        assertEquals(expectedExistence, existence);
    }
}
