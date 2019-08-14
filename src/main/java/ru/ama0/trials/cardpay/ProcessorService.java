package ru.ama0.trials.cardpay;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.ama0.trials.cardpay.readers.FileRecordReaderFactory;
import ru.ama0.trials.cardpay.util.FileUtils;
import ru.ama0.trials.cardpay.writers.RecordWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

@Service
@Slf4j
public class ProcessorService {
    private static final int TERMINATION_WAIT_SECONDS = 1;

    private final FileRecordReaderFactory factory;
    private final RecordWriter recordWriter;

    @Value("${readers.threads.max.number}")
    private int maxNumberOfReaderThreads = 5;

    @Autowired
    public ProcessorService(FileRecordReaderFactory factory, RecordWriter recordWriter) {
        this.factory = factory;
        this.recordWriter = recordWriter;
    }

    public void process(String... fileNames) {
        Set<File> files;
        try {
            files = FileUtils.convertAndCheckFilesExist(fileNames);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return;
        }

        ExecutorService readerServicePool = Executors.newFixedThreadPool(maxNumberOfReaderThreads);
        ExecutorService writerService = Executors.newSingleThreadExecutor();

        Future<Void> writerFuture = writerService.submit(recordWriter);

        List<Future<Void>> readerFutures = new ArrayList<>(fileNames.length);
        for (File file : files) {
            readerFutures.add(readerServicePool.submit(factory.get(file)));
        }

        for (Future<Void> readerFuture : readerFutures) {
            try {
                readerFuture.get();
            } catch (ExecutionException e) {
                log.error("Error during file reading", e);
            } catch (InterruptedException e) {
                log.error("Reading interrupted");
            }
        }
        readerServicePool.shutdown();

        writerFuture.cancel(true);
        writerService.shutdown();
        try {
            while (!writerService.isTerminated()) {
                writerService.awaitTermination(TERMINATION_WAIT_SECONDS, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            log.error("Writer service termination wait interrupted");
        }
    }
}
