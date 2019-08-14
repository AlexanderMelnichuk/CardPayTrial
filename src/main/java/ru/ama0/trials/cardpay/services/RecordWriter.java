package ru.ama0.trials.cardpay.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ama0.trials.cardpay.data.Record;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Component
public class RecordWriter implements Callable<Void> {

    private static final int POLL_TIMEOUT_SECONDS = 1;
    private BlockingQueue<Record> writeQueue;

    @Autowired
    public RecordWriter(BlockingQueue<Record> writeQueue) {
        this.writeQueue = writeQueue;
    }

    public Void call() throws Exception {
        boolean isInterrupted = false;

        ObjectMapper objectMapper = new ObjectMapper();
        Record record;
        while (!writeQueue.isEmpty() || !isInterrupted) {
            try {
                record = writeQueue.poll(POLL_TIMEOUT_SECONDS, TimeUnit.SECONDS);
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
