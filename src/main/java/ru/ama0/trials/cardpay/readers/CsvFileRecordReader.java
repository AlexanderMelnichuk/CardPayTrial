package ru.ama0.trials.cardpay.readers;

import org.springframework.beans.factory.annotation.Value;
import ru.ama0.trials.cardpay.data.ConstraintValidator;
import ru.ama0.trials.cardpay.data.Record;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class CsvFileRecordReader extends AbstractFileRecordReader {

    ConstraintValidator<Record> validator = new ConstraintValidator<>();

    @Value("${reader.csv.separator}")
    private String csvSeparator = ",";

    public CsvFileRecordReader(File file, BlockingQueue<Record> queue) {
        super(file, queue);
    }

    @Override
    public Void call() throws Exception {
        Record record;
        try (FileReader fileReader = new FileReader(file);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            long lineNumber = 1;
            while (bufferedReader.ready()) {
                String row = bufferedReader.readLine();
                String[] fields = row.split(csvSeparator);
                if (fields.length < 4) {
                    record = Record.builder()
                            .orderId(fields[0])
                            .amount(fields.length > 1 ? fields[1] : "")
                            .currency(fields.length > 2 ? fields[2] : "")
                            .comment("")
                            .filename(file.getName())
                            .line(lineNumber)
                            .result(String.format("Missing %d required field(s)", 4 - fields.length))
                            .build();
                } else {
                    record = Record.builder()
                            .orderId(fields[0])
                            .amount(fields[1])
                            .currency(fields[2])
                            .comment(fields[3])
                            .filename(file.getName())
                            .line(lineNumber)
                            .build();
                }
                lineNumber++;
                List<String> errors = validator.validate(record);
                if (!errors.isEmpty()) {
                    record.setResult(errors.stream().reduce("", String::concat));
                }
                queue.put(record);
            }
        }
        return null;
    }
}
