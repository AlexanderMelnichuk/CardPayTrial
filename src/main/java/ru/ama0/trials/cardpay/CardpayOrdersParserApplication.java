package ru.ama0.trials.cardpay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CardpayOrdersParserApplication implements CommandLineRunner {

    private final ProcessorService processorService;

    @Autowired
    public CardpayOrdersParserApplication(ProcessorService processorService) {
        this.processorService = processorService;
    }

    public static void main(String[] args) {
        SpringApplication.run(CardpayOrdersParserApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (args.length < 1) {
            return;
        }
        processorService.process(args);
    }
}
