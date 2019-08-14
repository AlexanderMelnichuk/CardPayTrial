package ru.ama0.trials.cardpay.readers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ama0.trials.cardpay.util.FileUtils;
import ru.ama0.trials.cardpay.data.Record;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.function.BiFunction;

@Component
public class FileRecordReaderFactory {
    private static final Map<String, BiFunction<File, BlockingQueue<Record>, FileRecordReader>> readers;

    static {
        readers = new HashMap<>();
        readers.put("csv", CsvFileRecordReader::new);
        readers.put("json", JsonFileRecordReader::new);
        // Add readers for other formats (e.g. xlsx) here if needed
    }

    private BlockingQueue<Record> queue;

    @Autowired
    public FileRecordReaderFactory(BlockingQueue<Record> queue) {
        this.queue = queue;
    }

    public FileRecordReader get(File inputFile) {
        if (inputFile == null) {
            throw new IllegalArgumentException("Input filename must not be null.");
        }

        String fileExtension = FileUtils.getFileExtension(inputFile);

        return readers
                .getOrDefault(fileExtension, this::throwExtensionNotSupported)
                .apply(inputFile, queue);
    }

    private FileRecordReader throwExtensionNotSupported(File file, BlockingQueue<Record> queue) {
        throw new UnsupportedOperationException(String.format("File (%s) has unsupported extension", file.getName()));
    }
}
