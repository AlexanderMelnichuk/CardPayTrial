package ru.ama0.trials.cardpay.services.readers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import ru.ama0.trials.cardpay.CardpayOrdersParserApplication;

import java.io.File;

import static ru.ama0.trials.cardpay.utils.FileUtilsTest.getFileByName;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {CardpayOrdersParserApplication.class})
@TestPropertySource(locations="classpath:application.properties")
public class FileRecordReaderFactoryTest {

    @Autowired
    FileRecordReaderFactory factory;

    @Test
    public void givenSupportedExtensionWhenGetThenReturnProperFileRecordReader() {
        // Act
        FileRecordReader csvReader = factory.get(getFileByName("test.csv"));
        FileRecordReader jsonReader = factory.get(getFileByName("test.json"));

        // Assert
        Assert.isAssignable(CsvFileRecordReader.class, csvReader.getClass());
        Assert.isAssignable(JsonFileRecordReader.class, jsonReader.getClass());
    }

    @Test (expected = IllegalArgumentException.class)
    public void givenUnsupportedExtensionWhenGetThenThrow() {
        // Act & Assert
        FileRecordReader reader = factory.get(new File("test.unsupported"));
    }

    @Test (expected = IllegalArgumentException.class)
    public void givenNullFileWhenGetThenThrow() {
        // Act & Assert
        FileRecordReader reader = factory.get(null);
    }

}
