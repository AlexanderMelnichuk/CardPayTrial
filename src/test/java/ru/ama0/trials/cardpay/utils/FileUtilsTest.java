package ru.ama0.trials.cardpay.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class FileUtilsTest {

    @Test
    public void givenListOfExistingFilenamesWhenConvertAndCheckFilesExistThenConvert() {
        // Arrange
        String[] fileNames = new String[] {"src/test/resources/test.csv", "src/test/resources/test.json"};

        // Act
        Set<File> files = FileUtils.convertAndCheckFilesExist(fileNames);

        // Assert
        assertEquals(2, files.size());
    }
}
