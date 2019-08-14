package ru.ama0.trials.cardpay.data;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonPropertyOrder({"id", "amount", "currency", "comment", "filename", "line", "result"})
public class Record {

    private final String orderId;
    private final String amount;
    private final String currency;
    private final String comment;
    private String filename;
    private Long line;

    @Builder.Default
    private String result = "OK";

    @JsonGetter("id")
    public String getOrderId() {
        return orderId;
    }
}
