package ru.ama0.trials.cardpay.data;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RawRecord {
    private final String orderId;
    private final String amount;
    private final String currency;
    private final String comment;
    private String filename;
    private Long line;
    private String result;
}
