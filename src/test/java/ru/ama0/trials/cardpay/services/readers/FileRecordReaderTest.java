package ru.ama0.trials.cardpay.services.readers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockReset;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.TestPropertySource;
import ru.ama0.trials.cardpay.CardpayOrdersParserApplication;
import ru.ama0.trials.cardpay.data.RawRecord;
import ru.ama0.trials.cardpay.services.ReaderFactoryProvider;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.calls;
import static org.mockito.Mockito.inOrder;
import static ru.ama0.trials.cardpay.utils.FileUtilsTest.getFileByName;

@RunWith(Parameterized.class)
@ContextConfiguration(classes = {CardpayOrdersParserApplication.class})
@TestPropertySource(locations="classpath:application.properties")
public class FileRecordReaderTest {

    @SpyBean(reset = MockReset.BEFORE)
    private BlockingQueue<RawRecord> readQueue;

    @Autowired
    private ReaderFactoryProvider readerFactoryProvider;

    private String fileName;

    public FileRecordReaderTest(String fileName) {
        this.fileName = fileName;
    }

    @Parameterized.Parameters
    public static Collection archivesToTest() {
        return Arrays.asList(
                "test.csv",
                "test.json");
    }

    @Before
    public void setUpContext() throws Exception {
        // Enabling Spring Context without SpringRunner
        TestContextManager testContextManager = new TestContextManager(getClass());
        testContextManager.prepareTestInstance(this);

        // Preparing the queue for next run
        readQueue.clear();
    }

    @Test
    public void givenValidCsvWhenCsvReaderCallThenCreateRawRecords() throws Exception {
        // Arrange
        ExecutorService threadPool = Executors.newFixedThreadPool(1);
        File file = getFileByName(fileName);
        FileRecordReader reader = readerFactoryProvider.get(file);

        // Act
        Future<Void> future = threadPool.submit(reader);
        future.get();
        threadPool.shutdown();

        // Assert
        InOrder inOrder = inOrder(readQueue);
        inOrder.verify(readQueue, calls(31)).put(any());
        assertEquals(31, readQueue.size());

        readQueue.poll();
        RawRecord rawRecord2 = readQueue.poll();
        RawRecord rawRecord3 = readQueue.poll();

        assertEquals("1", rawRecord2.getOrderId());
        assertEquals("1893", rawRecord2.getAmount());
        assertEquals("оплата заказа", rawRecord2.getComment());
        assertEquals("USD", rawRecord2.getCurrency());
        assertEquals(fileName, rawRecord2.getFilename());
        assertEquals((Long)2L, rawRecord2.getLine());
        assertNull(rawRecord2.getResult());

        assertEquals("2", rawRecord3.getOrderId());
        assertEquals("3954", rawRecord3.getAmount());
        assertEquals("Счёт 855", rawRecord3.getComment());
        assertEquals("RUB", rawRecord3.getCurrency());
        assertEquals(fileName, rawRecord3.getFilename());
        assertEquals((Long)3L, rawRecord3.getLine());
        assertNull(rawRecord3.getResult());
    }
}
