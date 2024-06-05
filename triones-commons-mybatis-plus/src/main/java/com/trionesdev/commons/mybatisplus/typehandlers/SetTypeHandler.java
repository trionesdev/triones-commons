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
import java.util.Set;

@Slf4j
@MappedTypes({Set.class})
@MappedJdbcTypes(JdbcType.VARCHAR)
public abstract class SetTypeHandler<T> extends CollectionTypeHandler<T> {

    public SetTypeHandler(Class<?> type) {
        super(type);
    }

    public SetTypeHandler(Class<?> type, Field field) {
        super(type, field);
    }

    @SneakyThrows
    @Override
    public Collection<T> parse(String json) {
        JavaType javaType = getObjectMapper().getTypeFactory().constructCollectionType(Set.class, specificType());
        return getObjectMapper().readValue(json, javaType);
    }
}