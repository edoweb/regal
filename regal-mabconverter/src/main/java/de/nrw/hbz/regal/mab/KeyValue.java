package de.nrw.hbz.regal.mab;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "key", "value" })
public class KeyValue {
    public KeyValue() {
	key = null;
	value = null;
    }

    public String key;
    public String value;
}
