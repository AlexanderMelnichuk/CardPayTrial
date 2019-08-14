package ru.ama0.trials.cardpay.readers;

import java.util.concurrent.Callable;

public interface FileRecordReader extends Callable<Void> {
    Void call() throws Exception;
}
