package ru.ama0.trials.cardpay.readers;

import lombok.RequiredArgsConstructor;
import ru.ama0.trials.cardpay.data.Record;

import java.io.File;
import java.util.concurrent.BlockingQueue;

@RequiredArgsConstructor
public abstract class AbstractFileRecordReader implements FileRecordReader {

    protected final File file;
    protected final BlockingQueue<Record> queue;

}
