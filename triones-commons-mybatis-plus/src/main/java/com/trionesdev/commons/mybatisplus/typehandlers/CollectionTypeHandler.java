package com.trionesdev.commons.mybatisplus.typehandlers;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@MappedTypes({Collection.class})
@MappedJdbcTypes(JdbcType.VARCHAR)
public abstract class CollectionTypeHandler<T> extends AbstractJsonTypeHandler<Collection<T>> {
    private static ObjectMapper OBJECT_MAPPER;


    public CollectionTypeHandler(Class<?> type) {
        super(type);
    }

    public CollectionTypeHandler(Class<?> type, Field field) {
        super(type, field);
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
    public Collection<T> parse(String json) {
        JavaType javaType = getObjectMapper().getTypeFactory().constructCollectionType(Collection.class, specificType());
        return getObjectMapper().readValue(json, javaType);
    }

    @SneakyThrows
    @Override
    public String toJson(Object obj) {
        return getObjectMapper().writeValueAsString(obj);
    }

    protected abstract Class<T> specificType();

}