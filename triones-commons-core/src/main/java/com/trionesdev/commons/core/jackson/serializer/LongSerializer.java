package com.trionesdev.commons.core.jackson.serializer;

import java.io.IOException;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class LongSerializer extends JsonSerializer<Long> {

    public final static LongSerializer instance = new LongSerializer();

    @Override
    public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if(Objects.nonNull(value)){
            if(value <= Integer.MAX_VALUE){
                gen.writeNumber(value);
            }else{
                gen.writeString(value.toString());
            }
        }
    }
}
