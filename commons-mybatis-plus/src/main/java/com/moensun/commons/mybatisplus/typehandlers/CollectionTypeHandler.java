package com.moensun.commons.mybatisplus.typehandlers;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.util.Collection;
import java.util.List;

@Slf4j
@MappedTypes({List.class})
@MappedJdbcTypes(JdbcType.VARCHAR)
public class CollectionTypeHandler<T> extends AbstractJsonTypeHandler<Collection<T>> {
    private static ObjectMapper OBJECT_MAPPER;
    private final Class<?> type;

    public CollectionTypeHandler(Class<?> type) {
        if (log.isTraceEnabled()) {
            log.trace("JacksonTypeHandler(" + type + ")");
        }
        Assert.notNull(type, "Type argument cannot be null");
        this.type = type;
    }

    public static ObjectMapper getObjectMapper() {
        if (null == OBJECT_MAPPER) {
            OBJECT_MAPPER = new ObjectMapper();
        }
        return OBJECT_MAPPER;
    }

    public static void setObjectMapper(ObjectMapper objectMapper) {
        Assert.notNull(objectMapper, "ObjectMapper should not be null");
        CollectionTypeHandler.OBJECT_MAPPER = objectMapper;
    }

    @SneakyThrows
    @Override
    protected Collection<T> parse(String json) {
        return getObjectMapper().readValue(json, new TypeReference<Collection<T>>() {});
    }

    @SneakyThrows
    @Override
    protected String toJson(Collection<T> obj) {
        return getObjectMapper().writeValueAsString(obj);
    }
}
