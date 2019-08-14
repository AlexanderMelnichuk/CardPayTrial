package ru.ama0.trials.cardpay.writers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ama0.trials.cardpay.data.Record;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
public class JsonRecordWriter implements RecordWriter {

    private static final int POLL_TIMEOUT_SECONDS = 1;
    private BlockingQueue<Record> queue;

    @Autowired
    public JsonRecordWriter(BlockingQueue<Record> queue) {
        this.queue = queue;
    }

    public Void call() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Record record;
        boolean isInterrupted = false;
        while (!queue.isEmpty() || !isInterrupted) {
            try {
                record = queue.poll(POLL_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                if (record != null) {
                    System.out.println(objectMapper.writeValueAsString(record));
                }
            } catch (InterruptedException e) {
                isInterrupted = true;
            }
        }
        return null;
    }
}
