package ru.ama0.trials.cardpay.data;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Record {
    private final Long id;
    private final Double amount;
    private final String currency;
    private final String comment;
    private String filename;
    private Long line;
    private String result;
}
