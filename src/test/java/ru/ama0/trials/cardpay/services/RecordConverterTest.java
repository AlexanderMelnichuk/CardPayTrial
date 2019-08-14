package ru.ama0.trials.cardpay.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ru.ama0.trials.cardpay.CardpayOrdersParserApplication;
import ru.ama0.trials.cardpay.config.SpringConfiguration;
import ru.ama0.trials.cardpay.data.RawRecord;
import ru.ama0.trials.cardpay.data.Record;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.calls;
import static org.mockito.Mockito.inOrder;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {CardpayOrdersParserApplication.class})
@TestPropertySource(locations="classpath:application.properties")
public class RecordConverterTest {

    @Autowired
    private BlockingQueue<RawRecord> readQueue;

    @SpyBean
    private BlockingQueue<Record> writeQueue;

    @Autowired
    RecordConverter recordConverter;

    public static final List<String> CURRENCIES =
            Collections.unmodifiableList(Arrays.asList("RUR", "USD", "JPY", "KRW", "RUR"));

    @Test
    public void givenValidRawRecordsWhenRecordConverterCallThenCreateOkRecords() throws Exception {
        // Arrange
        List<RawRecord> rawRecordList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            rawRecordList.add(RawRecord.builder()
                    .orderId(String.valueOf(i))
                    .amount(String.valueOf(100 * i))
                    .comment("Some record " + i)
                    .currency(CURRENCIES.get(i))
                    .filename(String.format("file%d.txt", (i + 1) / 2))
                    .line(i + 5L)
                    .build()
            );
        }

        for (RawRecord rawRecord : rawRecordList) {
            try {
                readQueue.put(rawRecord);
            } catch (InterruptedException e) {
                // ignore
            }
        }

        ExecutorService threadPool = Executors.newFixedThreadPool(1);
        CountDownLatch latch = new CountDownLatch(1);

        // Act
        Future<Void> future = threadPool.submit(recordConverter);

        while (!readQueue.isEmpty()) {
            latch.await(50, TimeUnit.MILLISECONDS);
        }
        future.cancel(true);
        threadPool.shutdown();

        // Assert
        InOrder inOrder = inOrder(writeQueue);
        inOrder.verify(writeQueue, calls(5)).put(any());
        for(RawRecord rawRecord : rawRecordList) {
            Record record = writeQueue.poll();
            assertEquals(rawRecord.getOrderId(), record.getId().toString());
            assertEquals((Double) Double.parseDouble(rawRecord.getAmount()), record.getAmount());
            assertEquals(rawRecord.getComment(), record.getComment());
            assertEquals(rawRecord.getCurrency(), record.getCurrency());
            assertEquals(rawRecord.getFilename(), record.getFilename());
            assertEquals(rawRecord.getLine(), record.getLine());
            assertEquals("OK", record.getResult());
        }
    }

}
