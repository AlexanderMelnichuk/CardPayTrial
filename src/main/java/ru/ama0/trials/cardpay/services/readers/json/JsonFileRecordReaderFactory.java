package ru.ama0.trials.cardpay.services.readers.json;

import lombok.NonNull;
import org.springframework.stereotype.Component;
import ru.ama0.trials.cardpay.data.RawRecord;
import ru.ama0.trials.cardpay.services.readers.FileRecordReader;
import ru.ama0.trials.cardpay.services.readers.FileRecordReaderFactory;

import java.io.File;
import java.util.concurrent.BlockingQueue;

@Component
public class JsonFileRecordReaderFactory implements FileRecordReaderFactory {
    @Override
    public String supportedExtension() {
        return "json";
    }

    @Override
    public FileRecordReader getReader(@NonNull File file, @NonNull BlockingQueue<RawRecord> readQueue) {
        return new JsonFileRecordReader(file, readQueue);
    }
}
