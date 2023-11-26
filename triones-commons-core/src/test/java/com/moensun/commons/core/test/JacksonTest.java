package com.moensun.commons.core.test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JacksonTest {

    private final static ObjectMapper mapper;

    static {
        mapper = initMapper();
    }

    public static ObjectMapper initMapper() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Instant.class,InstantSerializer.INSTANCE);
        module.addDeserializer(Instant.class, InstantDeserializer.INSTANT);
        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
//        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        mapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
//        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
//        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
//        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(module);
        return mapper;
    }

    @Test
    public void test1() throws JsonProcessingException {
        Person person = createPerson();
        String jsonString = mapper.writeValueAsString(person);
        System.out.println(jsonString);
        String jsonStr = "{\"name\":\"小李\",\"age\":19}";
        Person person1 = mapper.readValue(jsonStr,Person.class);
        System.out.println(person1);
    }

    @Test
    public void test2() throws JsonProcessingException {
        String jsonStr = "{\"name\":\"小李\",\"age\":19,\"gender\":\"MAN\"}";
        Person person = mapper.readValue(jsonStr,Person.class);
        System.out.println(person);
    }

    @Test
    public void test3() throws JsonProcessingException {
        Person person = createPerson();
        String jsonString = mapper.writeValueAsString(person);
        System.out.println(jsonString);
    }

    @Test
    public void test4() throws JsonProcessingException {
        Person person = createPerson();
        String jsonString = mapper.writeValueAsString(person);
        System.out.println(jsonString);
        String jsonStr = "{\"name\":\"小李\",\"age\":19,\"instant\":1658053120673}";
        Person person1 = mapper.readValue(jsonStr,Person.class);
        System.out.println(person1);
    }

    @Test
    public void test5() throws JsonProcessingException {
        Person person = createPerson();
        String jsonString = mapper.writeValueAsString(person);
        System.out.println(jsonString);
        String jsonStr = "{\"name\":\"小李\",\"age\":19,\"openid\":\"12345\"}";
        Person person1 = mapper.readValue(jsonStr,Person.class);
        System.out.println(person1);
    }

    @Test
    public void test6() throws JsonProcessingException {
        Person person = createPerson();
        String jsonString = mapper.writeValueAsString(Lists.newArrayList(person));
        System.out.println(jsonString);
        String jsonStr = "[{\"name\":\"小李\",\"age\":19}]";
        List<Person> person1 = mapper.readValue(jsonStr, new TypeReference<List<Person>>() {
        });
        System.out.println(person1);
        JavaType javaType = mapper.getTypeFactory().constructCollectionType(List.class,Person.class);
        List<Person> person2 = mapper.readValue(jsonStr, javaType);
        System.out.println(person2);
    }

    @Test
    public void test7() throws JsonProcessingException {
        Person person = createPerson();
        Map<String,Person> personMap = new HashMap<>();
        personMap.put("123",person);
        String jsonString = mapper.writeValueAsString(personMap);
        System.out.println(jsonString);
        String jsonStr = "{\"123\":{\"name\":\"小李\",\"age\":19}}";
        Map<String,Person> person1 = mapper.readValue(jsonStr, new TypeReference<HashMap<String,Person>>() {
        });
        System.out.println(person1);
        JavaType javaType = mapper.getTypeFactory().constructMapType(HashMap.class,String.class,Person.class);
        Map<String,Person> person2 = mapper.readValue(jsonStr, javaType);
        System.out.println(person2);
    }



    public Person createPerson() {
        return Person.builder().name("小王").age(12).build();
    }

//    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Person {
        private String name;
        private Integer age;
//        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
//        private Date date;
//        @JsonSerialize(using = InstantSerializer.class)
//        @JsonDeserialize(using = InstantDeserializer.class)
//        private Instant instant;
//        @JsonProperty(value = "openid")
//        private String openId;
    }

}
