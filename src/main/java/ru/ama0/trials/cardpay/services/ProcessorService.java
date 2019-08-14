package ru.ama0.trials.cardpay.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import ru.ama0.trials.cardpay.services.readers.FileRecordReaderFactory;
import ru.ama0.trials.cardpay.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

@Service
@Slf4j
public class ProcessorService {
    private static final int TERMINATION_WAIT_SECONDS = 5;

    private final FileRecordReaderFactory recordReaderFactory;
    private final RecordWriter recordWriter;

    @Value("${readers.threads.max.number}")
    private int maxNumberOfReaderThreads = 5;

    @Value("${converters.threads.max.number}")
    private int maxNumberOfConverterThreads = 5;

    @Autowired
    ApplicationContext context;

    @Autowired
    public ProcessorService(FileRecordReaderFactory recordReaderFactory, RecordWriter recordWriter) {
        this.recordReaderFactory = recordReaderFactory;
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
        ExecutorService converterServicePool = Executors.newFixedThreadPool(maxNumberOfConverterThreads);
        ExecutorService writerService = Executors.newSingleThreadExecutor();

        Future<Void> writerFuture = writerService.submit(recordWriter);

        List<Future<Void>> converterFutures = new ArrayList<>(maxNumberOfConverterThreads);
        for (int i = 0; i < maxNumberOfConverterThreads; i++) {
            converterFutures.add(converterServicePool.submit(context.getBean(RecordConverter.class)));
        }

        List<Future<Void>> readerFutures = new ArrayList<>(fileNames.length);
        for (File file : files) {
            readerFutures.add(readerServicePool.submit(recordReaderFactory.get(file)));
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

        for (Future<Void> converterFuture : converterFutures) {
            converterFuture.cancel(true);
        }
        shutdownExecutor(converterServicePool, "Converters service pool");

        writerFuture.cancel(true);
        shutdownExecutor(writerService, "Writer service");
    }

    private void shutdownExecutor(ExecutorService executor, String serviceName) {
        executor.shutdown();
        try {
            while (!executor.isTerminated()) {
                executor.awaitTermination(TERMINATION_WAIT_SECONDS, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            log.error("{} termination wait interrupted", serviceName);
        }
    }
}
