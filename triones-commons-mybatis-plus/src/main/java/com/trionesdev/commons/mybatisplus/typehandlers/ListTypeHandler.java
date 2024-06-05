package com.trionesdev.commons.mybatisplus.typehandlers;

import com.fasterxml.jackson.databind.JavaType;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

@Slf4j
@MappedTypes({List.class})
@MappedJdbcTypes(JdbcType.VARCHAR)
public abstract class ListTypeHandler<T> extends CollectionTypeHandler<T> {

    public ListTypeHandler(Class<?> type) {
        super(type);
    }

    public ListTypeHandler(Class<?> type, Field field) {
        super(type, field);
    }

    @SneakyThrows
    @Override
    public Collection<T> parse(String json) {
        JavaType javaType = getObjectMapper().getTypeFactory().constructCollectionType(List.class, specificType());
        return getObjectMapper().readValue(json, javaType);
    }
}