package ru.ama0.trials.cardpay.writers;

import java.util.concurrent.Callable;

public interface RecordWriter extends Callable<Void> {
    Void call() throws Exception;
}
