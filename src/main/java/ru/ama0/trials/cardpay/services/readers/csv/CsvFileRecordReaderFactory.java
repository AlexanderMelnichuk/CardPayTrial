package ru.ama0.trials.cardpay.services.readers.csv;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import ru.ama0.trials.cardpay.data.RawRecord;
import ru.ama0.trials.cardpay.services.readers.FileRecordReader;
import ru.ama0.trials.cardpay.services.readers.FileRecordReaderFactory;

import java.io.File;
import java.util.concurrent.BlockingQueue;

@Component
@AllArgsConstructor
public class CsvFileRecordReaderFactory implements FileRecordReaderFactory {
    private ApplicationContext context;

    @Override
    public String supportedExtension() {
        return "csv";
    }

    @Override
    public FileRecordReader getReader(@NonNull File file, @NonNull BlockingQueue<RawRecord> readQueue) {
        return context.getBean(CsvFileRecordReader.class, file, readQueue);
    }
}
