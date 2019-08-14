package ru.ama0.trials.cardpay.readers;

import ru.ama0.trials.cardpay.data.Record;

import java.io.File;
import java.util.concurrent.BlockingQueue;

public class JsonFileRecordReader extends AbstractFileRecordReader {

    public JsonFileRecordReader(File file, BlockingQueue<Record> queue) {
        super(file, queue);
    }

    @Override
    public Void call() {
        return null;
    }
}
