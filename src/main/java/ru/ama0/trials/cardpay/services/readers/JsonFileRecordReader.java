package ru.ama0.trials.cardpay.services.readers;

import ru.ama0.trials.cardpay.data.RawRecord;

import java.io.File;
import java.util.concurrent.BlockingQueue;

public class JsonFileRecordReader extends AbstractFileRecordReader {

    public JsonFileRecordReader(File file, BlockingQueue<RawRecord> queue) {
        super(file, queue);
    }

    @Override
    public Void call() {
        return null;
    }
}
