package com.trionesdev.commons.core.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import com.trionesdev.commons.core.ex.JsonException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * json 工具类
 */
@Slf4j
public class JacksonUtils {

    private static volatile ObjectMapper OBJECT_MAPPER = initMapper();

    private JacksonUtils() {
    }

    public static ObjectMapper getObjectMapper() {
        ObjectMapper mapper = OBJECT_MAPPER;
        if (mapper == null) {
            synchronized (JacksonUtils.class) {
                mapper = OBJECT_MAPPER;
                if (mapper == null) {
                    mapper = initMapper();
                    OBJECT_MAPPER = mapper;
                }
            }
        }
        return mapper;
    }

    public static void setObjectMapper(ObjectMapper objectMapper) {
        OBJECT_MAPPER = objectMapper;
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
        return execute(() -> getObjectMapper().writeValueAsString(object));
    }

    public static <T> T parse(String jsonString, Class<T> clazz) {
        if (StringUtils.isBlank(jsonString)) {
            return null;
        }
        return execute(() -> getObjectMapper().readValue(jsonString, clazz));
    }

    public static <T> T parse(String jsonString, TypeReference<T> valueTypeRef) {
        if (StringUtils.isBlank(jsonString)) {
            return null;
        }
        return execute(() -> getObjectMapper().readValue(jsonString, valueTypeRef));
    }

    public static <T> T parse(String jsonString, JavaType javaType) {
        if (StringUtils.isBlank(jsonString)) {
            return null;
        }
        return execute(() -> getObjectMapper().readValue(jsonString, javaType));
    }

    public static <T> T parse(byte[] bytes, Class<T> clazz) {
        if (ArrayUtils.isEmpty(bytes)) {
            return null;
        }
        return execute(() -> getObjectMapper().readValue(bytes, clazz));
    }

    public static <T> T parse(byte[] bytes, TypeReference<T> valueTypeRef) {
        if (ArrayUtils.isEmpty(bytes)) {
            return null;
        }
        return execute(() -> getObjectMapper().readValue(bytes, valueTypeRef));
    }

    public static <T> T parse(byte[] bytes, JavaType javaType) {
        if (ArrayUtils.isEmpty(bytes)) {
            return null;
        }
        return execute(() -> getObjectMapper().readValue(bytes, javaType));
    }

    public static <T> Collection<T> parseCollection(String jsonString, Class<T> clazz) {
        return parse(jsonString, constructCollectionType(Collection.class, clazz));
    }

    public static <T> Collection<T> parseCollection(byte[] bytes, Class<T> clazz) {
        return parse(bytes, constructCollectionType(Collection.class, clazz));
    }

    public static <T> List<T> parseList(String jsonString, Class<T> clazz) {
        return parse(jsonString, constructCollectionType(List.class, clazz));
    }

    public static <T> List<T> parseList(byte[] bytes, Class<T> clazz) {
        return parse(bytes, constructCollectionType(List.class, clazz));
    }

    public static <T> Set<T> parseSet(String jsonString, Class<T> clazz) {
        return parse(jsonString, constructCollectionType(Set.class, clazz));
    }

    public static <T> Set<T> parseSet(byte[] bytes, Class<T> clazz) {
        return parse(bytes, constructCollectionType(Set.class, clazz));
    }

    /**
     * 当 JSON 里只含有 Bean 的部分属性时，更新一个已存在 Bean，只覆盖该部分的属性.
     */
    public static void update(Object object, String jsonString) {
        try {
            getObjectMapper().readerForUpdating(object).readValue(jsonString);
        } catch (IOException e) {
            log.error("update json string:" + jsonString + " to object:" + object + " error.", e);
        }
    }

    public static <T> T treeToValue(TreeNode n, Class<T> valueType) throws JsonProcessingException {
        return getObjectMapper().treeToValue(n, valueType);
    }

    public static <T> T treeToValue(TreeNode n, JavaType javaType) throws JsonProcessingException {
        return getObjectMapper().treeToValue(n, javaType);
    }

    public static <T extends JsonNode> T valueToTree(Object fromValue) {
        return getObjectMapper().valueToTree(fromValue);
    }

    public static <T> T convertValue(Object fromValue, Class<T> toValueType) {
        return getObjectMapper().convertValue(fromValue, toValueType);
    }

    public static <T> T convertValue(Object fromValue, TypeReference<T> toValueTypeRef) {
        return getObjectMapper().convertValue(fromValue, toValueTypeRef);
    }

    public static <T> T convertValue(Object fromValue, JavaType toValueType) {
        return getObjectMapper().convertValue(fromValue, toValueType);
    }

    private static <T> T execute(IOCallable<T> callable) {
        try {
            return callable.call();
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            throw new JsonException(ex.getMessage());
        }
    }

    @FunctionalInterface
    private interface IOCallable<T> {
        T call() throws IOException;
    }
}
