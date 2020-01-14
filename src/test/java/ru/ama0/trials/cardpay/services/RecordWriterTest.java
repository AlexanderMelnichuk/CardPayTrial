package ru.ama0.trials.cardpay.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ru.ama0.trials.cardpay.CardpayOrdersParserApplication;
import ru.ama0.trials.cardpay.data.Record;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;
import static ru.ama0.trials.cardpay.services.RecordConverterTest.CURRENCIES;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {CardpayOrdersParserApplication.class})
@TestPropertySource(locations = "classpath:application.properties")
public class RecordWriterTest {

    @Autowired
    private BlockingQueue<Record> writeQueue;

    @Autowired
    private RecordWriter recordWriter;

    @Test
    public void givenValidRecordWhenRecordWriterCallThenPrintJsonToStdOut() throws Exception {
        // Arrange
        List<Record> recordList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            recordList.add(Record.builder()
                    .id((long) i)
                    .amount(100.0 * i)
                    .comment("Some record " + i)
                    .currency(CURRENCIES.get(i))
                    .filename(String.format("file%d.txt", (i + 1) / 2))
                    .line(i + 5L)
                    .build()
            );
        }

        for (Record record : recordList) {
            try {
                writeQueue.put(record);
            } catch (InterruptedException e) {
                // ignore
            }
        }

        ExecutorService threadExecutor = Executors.newSingleThreadExecutor();
        CountDownLatch latch = new CountDownLatch(1);

        ByteArrayOutputStream baos = redirectStdOut();

        // Act
        Future<Void> future = threadExecutor.submit(recordWriter);
        while (!writeQueue.isEmpty()) {
            latch.await(50, TimeUnit.MILLISECONDS);
        }

        future.cancel(true);
        threadExecutor.shutdown();

        // Assert
        recoverStdOut();

        String expectedOutput =
                "{\"id\":0,\"amount\":0.0,\"currency\":\"RUR\",\"comment\":\"Some record 0\",\"filename\":\"file0.txt\",\"line\":5}" + System.lineSeparator() +
                "{\"id\":1,\"amount\":100.0,\"currency\":\"USD\",\"comment\":\"Some record 1\",\"filename\":\"file1.txt\",\"line\":6}" + System.lineSeparator() +
                "{\"id\":2,\"amount\":200.0,\"currency\":\"JPY\",\"comment\":\"Some record 2\",\"filename\":\"file1.txt\",\"line\":7}" + System.lineSeparator() +
                "{\"id\":3,\"amount\":300.0,\"currency\":\"KRW\",\"comment\":\"Some record 3\",\"filename\":\"file2.txt\",\"line\":8}" + System.lineSeparator() +
                "{\"id\":4,\"amount\":400.0,\"currency\":\"RUR\",\"comment\":\"Some record 4\",\"filename\":\"file2.txt\",\"line\":9}" + System.lineSeparator();
        assertEquals(expectedOutput, baos.toString());
    }

    private ByteArrayOutputStream redirectStdOut() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        return baos;
    }

    private void recoverStdOut() {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
    }

}
