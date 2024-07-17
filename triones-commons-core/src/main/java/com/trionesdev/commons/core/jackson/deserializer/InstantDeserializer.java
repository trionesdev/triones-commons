package com.trionesdev.commons.core.jackson.deserializer;

import java.io.IOException;
import java.time.Instant;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class InstantDeserializer extends JsonDeserializer<Instant> {

    public final static InstantDeserializer instance = new InstantDeserializer();

    @Override
    public Instant deserialize(JsonParser p, DeserializationContext ctx) throws IOException, JsonProcessingException {
        if (StringUtils.isNoneBlank(p.getText())) {
            if (StringUtils.isNumeric(p.getText())) {
                return Instant.ofEpochMilli(p.getLongValue());
            } else {
                return Instant.parse(p.getText());
            }
        } else {
            return null;
        }
    }
}
