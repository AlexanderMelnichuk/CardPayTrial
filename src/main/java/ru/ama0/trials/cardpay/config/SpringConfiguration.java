package ru.ama0.trials.cardpay.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.ama0.trials.cardpay.data.RawRecord;
import ru.ama0.trials.cardpay.data.Record;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class SpringConfiguration {
    @Bean
    public BlockingQueue<RawRecord> readQueue() {
        return new LinkedBlockingQueue<>();
    }

    @Bean
    public BlockingQueue<Record> writeQueue() {
        return new LinkedBlockingQueue<>();
    }
}
