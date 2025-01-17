package ru.ama0.trials.cardpay.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.ama0.trials.cardpay.data.RawRecord;
import ru.ama0.trials.cardpay.data.Record;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
@Slf4j
public class RecordConverter implements Callable<Void> {
    private static final int POLL_TIMEOUT_SECONDS = 1;
    private static final String ERR_ORDER_ID_NUMBER = "orderId '%s' is not a number";
    private static final String ERR_AMOUNT_NUMBER = "amount '%s' is not a number";
    private static final String RESULT_OK = "OK";

    private final BlockingQueue<RawRecord> readQueue;
    private final BlockingQueue<Record> writeQueue;
    private volatile boolean isInterrupted;

    @Autowired
    public RecordConverter(BlockingQueue<RawRecord> readQueue, BlockingQueue<Record> writeQueue) {
        this.readQueue = readQueue;
        this.writeQueue = writeQueue;
    }

    public Void call() {
        isInterrupted = false;
        RawRecord rawRecord;
        while (!readQueue.isEmpty() || !isInterrupted) {
            try {
                rawRecord = readQueue.poll(POLL_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                if (rawRecord != null) {
                    Record record = convert(rawRecord);
                    sendRecordToQueueWithoutInterruptions(record);
                }
            } catch (InterruptedException e) {
                isInterrupted = true;
            }
        }
        return null;
    }

    private Record convert(RawRecord rawRecord) {
        List<String> results = new ArrayList<>();
        if (!StringUtils.isEmpty(rawRecord.getResult())) {
            results.add(rawRecord.getResult());
        }

        long id;
        try {
            id = Long.parseLong(rawRecord.getOrderId());
        } catch (NumberFormatException e) {
            id = 0L;
            results.add(String.format(ERR_ORDER_ID_NUMBER, rawRecord.getOrderId()));
        }

        double amount;
        try {
            amount = Double.parseDouble(rawRecord.getAmount());
        } catch (NumberFormatException e) {
            amount = Double.NaN;
            results.add(String.format(ERR_AMOUNT_NUMBER, rawRecord.getAmount()));
        }

        return Record.builder()
                .id(id)
                .amount(amount)
                .currency(rawRecord.getCurrency())
                .comment(rawRecord.getComment())
                .filename(rawRecord.getFilename())
                .line(rawRecord.getLine())
                .result(results.isEmpty() ? RESULT_OK : String.join("; ", results))
                .build();
    }

    private void sendRecordToQueueWithoutInterruptions(Record record) {
        while (true) {
            try {
                writeQueue.put(record);
                break;
            } catch (InterruptedException e) {
                isInterrupted = true;
            } catch (Exception e) {
                log.error("Other exception thrown", e);
            }
        }
    }
}
