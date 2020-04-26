package ru.ama0.trials.cardpay.services.readers;

import ru.ama0.trials.cardpay.data.RawRecord;

import java.io.File;
import java.util.concurrent.BlockingQueue;

public interface FileRecordReaderFactory {
    String supportedExtension();
    FileRecordReader getReader(File file, BlockingQueue<RawRecord> readQueue);
}
