package ru.ama0.trials.cardpay.services;

import org.springframework.stereotype.Service;
import ru.ama0.trials.cardpay.data.RawRecord;
import ru.ama0.trials.cardpay.services.readers.FileRecordReader;
import ru.ama0.trials.cardpay.services.readers.FileRecordReaderFactory;
import ru.ama0.trials.cardpay.utils.FileUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

@Service
public class ReaderFactoryProvider {

    private List<FileRecordReaderFactory> fileRecordReaderFactoryList;
    private BlockingQueue<RawRecord> readQueue;

    private Map<String, FileRecordReaderFactory> readers = new HashMap<>();

    public ReaderFactoryProvider(
            List<FileRecordReaderFactory> fileRecordReaderFactoryList,
            BlockingQueue<RawRecord> readQueue) {
        this.fileRecordReaderFactoryList = fileRecordReaderFactoryList;
        this.readQueue = readQueue;
    }

    @PostConstruct
    public void init() {
        for (FileRecordReaderFactory readerFactory: fileRecordReaderFactoryList) {
            readers.put(readerFactory.supportedExtension(), readerFactory);
        }
        readers = Collections.unmodifiableMap(readers);
    }

    public FileRecordReader get(File inputFile) {
        if (inputFile == null) {
            throw new IllegalArgumentException("Input filename must not be null.");
        }

        String fileExtension = FileUtils.getFileExtension(inputFile);

        if (!readers.containsKey(fileExtension)) {
            throw new IllegalArgumentException(String.format("File (%s) has unsupported extension", inputFile.getName()));
        }

        return readers.get(fileExtension).getReader(inputFile, readQueue);
    }

}
