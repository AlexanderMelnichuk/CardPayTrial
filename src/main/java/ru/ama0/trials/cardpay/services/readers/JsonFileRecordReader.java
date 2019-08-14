package ru.ama0.trials.cardpay.services.readers;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import lombok.extern.slf4j.Slf4j;
import ru.ama0.trials.cardpay.data.RawRecord;

import java.io.File;
import java.io.FileReader;
import java.util.concurrent.BlockingQueue;

@Slf4j
public class JsonFileRecordReader extends AbstractFileRecordReader {

    public JsonFileRecordReader(File file, BlockingQueue<RawRecord> queue) {
        super(file, queue);
    }

    @Override
    public Void call() throws Exception {

        Gson gson = new Gson();
        try (JsonReader reader = new JsonReader(new FileReader(file))) {
            reader.beginArray();
            long lineNumber = 1;
            while (reader.hasNext()) {
                RawRecord rawRecord = gson.fromJson(reader, RawRecord.class);
                rawRecord.setFilename(file.getName());
                rawRecord.setLine(lineNumber);
                readQueue.put(rawRecord);
                lineNumber++;
            }
            reader.endArray();
        }

        return null;
    }
}
