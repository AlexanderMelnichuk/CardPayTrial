package ru.ama0.trials.cardpay.services.readers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import ru.ama0.trials.cardpay.CardpayOrdersParserApplication;
import ru.ama0.trials.cardpay.services.ReaderFactoryProvider;
import ru.ama0.trials.cardpay.services.readers.csv.CsvFileRecordReader;
import ru.ama0.trials.cardpay.services.readers.json.JsonFileRecordReader;

import java.io.File;

import static ru.ama0.trials.cardpay.utils.FileUtilsTest.getFileByName;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {CardpayOrdersParserApplication.class})
@TestPropertySource(locations="classpath:application.properties")
public class ReaderFactoryProviderTest {

    @Autowired
    private ReaderFactoryProvider readerFactoryProvider;

    @Test
    public void givenSupportedExtensionWhenGetThenReturnProperFileRecordReader() {
        // Act
        FileRecordReader csvReader = readerFactoryProvider.get(getFileByName("test.csv"));
        FileRecordReader jsonReader = readerFactoryProvider.get(getFileByName("test.json"));

        // Assert
        Assert.isAssignable(CsvFileRecordReader.class, csvReader.getClass());
        Assert.isAssignable(JsonFileRecordReader.class, jsonReader.getClass());
    }

    @Test (expected = IllegalArgumentException.class)
    public void givenUnsupportedExtensionWhenGetThenThrow() {
        // Act & Assert
        readerFactoryProvider.get(new File("test.unsupported"));
    }

    @Test (expected = IllegalArgumentException.class)
    public void givenNullFileWhenGetThenThrow() {
        // Act & Assert
        readerFactoryProvider.get(null);
    }

}
