package com.trionesdev.commons.mybatisplus.typehandlers;

import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

@MappedTypes({Collection.class})
@MappedJdbcTypes({JdbcType.VARCHAR})
public abstract class SpecificTypeHandler<T> extends AbstractJsonTypeHandler<T> {
    private static final Logger log = LoggerFactory.getLogger(SpecificTypeHandler.class);

    private static ObjectMapper OBJECT_MAPPER;

    public SpecificTypeHandler() {
    }

    public static ObjectMapper getObjectMapper() {
        if (null == OBJECT_MAPPER) {
            OBJECT_MAPPER = new ObjectMapper();
        }

        return OBJECT_MAPPER;
    }

    @SneakyThrows
    @Override
    protected T parse(String json) {
        return getObjectMapper().readValue(json,typeReference());
    }

    @SneakyThrows
    @Override
    protected String toJson(T obj) {
        return getObjectMapper().writeValueAsString(obj);
    }

    public abstract TypeReference<T> typeReference();

}
