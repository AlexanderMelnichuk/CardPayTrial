package ru.ama0.trials.cardpay.services.readers;

import org.springframework.beans.factory.annotation.Value;
import ru.ama0.trials.cardpay.data.RawRecord;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.BlockingQueue;

public class CsvFileRecordReader extends AbstractFileRecordReader {

    @Value("${reader.csv.separator}")
    private String csvSeparator = ",";

    public CsvFileRecordReader(File file, BlockingQueue<RawRecord> readQueue) {
        super(file, readQueue);
    }

    @Override
    public Void call() throws Exception {
        try (FileReader fileReader = new FileReader(file);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            long lineNumber = 1;
            while (bufferedReader.ready()) {
                readQueue.put(createRawRecord(lineNumber, bufferedReader.readLine()));
                lineNumber++;
            }
        }
        return null;
    }

    private RawRecord createRawRecord(long lineNumber, String row) {
        RawRecord rawRecord;
        String[] fields = row.split(csvSeparator);
        if (fields.length < 4) {
            rawRecord = RawRecord.builder()
                    .orderId(fields[0])
                    .amount(fields.length > 1 ? fields[1] : "")
                    .currency(fields.length > 2 ? fields[2] : "")
                    .comment("")
                    .filename(file.getName())
                    .line(lineNumber)
                    .result(String.format("Missing %d required field(s)", 4 - fields.length))
                    .build();
        } else {
            rawRecord = RawRecord.builder()
                    .orderId(fields[0])
                    .amount(fields[1])
                    .currency(fields[2])
                    .comment(fields[3])
                    .filename(file.getName())
                    .line(lineNumber)
                    .build();
        }
        return rawRecord;
    }
}
