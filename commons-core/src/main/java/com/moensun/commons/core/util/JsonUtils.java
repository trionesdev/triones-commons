package com.moensun.commons.core.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import com.moensun.commons.core.ex.JsonException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class JsonUtils {
    private static ObjectMapper OBJECT_MAPPER;

    public static ObjectMapper getObjectMapper() {
        if (null == OBJECT_MAPPER) {
            OBJECT_MAPPER = initMapper();
        }
        return OBJECT_MAPPER;
    }

    public static void setObjectMapper(ObjectMapper objectMapper) {
        JsonUtils.OBJECT_MAPPER = objectMapper;
    }

    public static ObjectMapper initMapper() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Instant.class, InstantSerializer.INSTANCE);
        module.addDeserializer(Instant.class, InstantDeserializer.INSTANT);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(module);
        return mapper;
    }

    public static JavaType constructType(Class<?> clazz) {
        return getObjectMapper().constructType(clazz);
    }

    public static JavaType constructCollectionType(Class<? extends Collection> collectionClass,
                                                   Class<?> elementClass) {
        return getObjectMapper().getTypeFactory().constructCollectionType(collectionClass, elementClass);
    }

    public static JavaType constructMapType(Class<? extends Map> mapClass, Class<?> keyClass,
                                            Class<?> valueClass) {
        return getObjectMapper().getTypeFactory().constructMapType(mapClass, keyClass, valueClass);
    }


    public static String toJsonString(Object object) {
        try {
            return getObjectMapper().writeValueAsString(object);
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            throw new JsonException(ex.getMessage());
        }
    }

    public static <T> T parse(String jsonString, Class<T> clazz) {
        if (StringUtils.isBlank(jsonString)) {
            return null;
        }
        try {
            return getObjectMapper().readValue(jsonString, clazz);
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            throw new JsonException(ex.getMessage());
        }
    }

    @SuppressWarnings(value = "unchecked")
    public static <T> T parse(String jsonString, JavaType javaType) {
        if (StringUtils.isBlank(jsonString)) {
            return null;
        }

        try {
            return (T) getObjectMapper().readValue(jsonString, javaType);
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            throw new JsonException(ex.getMessage());
        }
    }

    public static <T> Collection<T> parseCollection(String jsonString, Class<T> clazz) {
        JavaType javaType = constructCollectionType(Collection.class, clazz);
        try {
            return getObjectMapper().readValue(jsonString, javaType);
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            throw new JsonException(ex.getMessage());
        }
    }

    public static <T> List<T> parseList(String jsonString, Class<T> clazz) {
        JavaType javaType = constructCollectionType(List.class, clazz);
        try {
            return getObjectMapper().readValue(jsonString, javaType);
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            throw new JsonException(ex.getMessage());
        }
    }

    public static <T> Set<T> parseSet(String jsonString, Class<T> clazz) {
        JavaType javaType = constructCollectionType(Set.class, clazz);
        try {
            return getObjectMapper().readValue(jsonString, javaType);
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            throw new JsonException(ex.getMessage());
        }
    }

    /**
     * 当JSON里只含有Bean的部分屬性時，更新一個已存在Bean，只覆蓋該部分的屬性.
     */
    public void update(Object object, String jsonString) {
        try {
            getObjectMapper().readerForUpdating(object).readValue(jsonString);
        } catch (IOException e) {
            log.error("update json string:" + jsonString + " to object:" + object + " error.", e);
        }
    }

}
