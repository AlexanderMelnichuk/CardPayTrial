package ru.ama0.trials.cardpay.services.readers;

import lombok.NonNull;
import ru.ama0.trials.cardpay.data.RawRecord;

import java.io.File;
import java.util.concurrent.BlockingQueue;

public abstract class AbstractFileRecordReader implements FileRecordReader {
    protected final File file;
    protected final BlockingQueue<RawRecord> readQueue;

    public AbstractFileRecordReader(@NonNull File file, @NonNull BlockingQueue<RawRecord> readQueue) {
        this.file = file;
        this.readQueue = readQueue;
    }
}
