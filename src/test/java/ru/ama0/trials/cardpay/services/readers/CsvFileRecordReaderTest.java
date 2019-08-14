package ru.ama0.trials.cardpay.services.readers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ru.ama0.trials.cardpay.CardpayOrdersParserApplication;
import ru.ama0.trials.cardpay.data.RawRecord;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.calls;
import static org.mockito.Mockito.inOrder;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {CardpayOrdersParserApplication.class})
@TestPropertySource(locations="classpath:application.properties")
public class CsvFileRecordReaderTest {
    private static final String CSV_FILENAME = "src/test/resources/test.csv";

    @SpyBean
    private BlockingQueue<RawRecord> readQueue;

    @Autowired
    FileRecordReaderFactory factory;

    @Test
    public void givenValidCsvWhenCsvReaderCallThenCreateRawRecords() throws Exception {
        // Arrange
        ExecutorService threadPool = Executors.newFixedThreadPool(1);
        FileRecordReader reader = factory.get(new File(CSV_FILENAME));

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
        assertEquals("test.csv", rawRecord2.getFilename());
        assertEquals((Long)2L, rawRecord2.getLine());
        assertNull(rawRecord2.getResult());

        assertEquals("2", rawRecord3.getOrderId());
        assertEquals("3954", rawRecord3.getAmount());
        assertEquals("Счёт 855", rawRecord3.getComment());
        assertEquals("RUB", rawRecord3.getCurrency());
        assertEquals("test.csv", rawRecord3.getFilename());
        assertEquals((Long)3L, rawRecord3.getLine());
        assertNull(rawRecord3.getResult());
    }
}
