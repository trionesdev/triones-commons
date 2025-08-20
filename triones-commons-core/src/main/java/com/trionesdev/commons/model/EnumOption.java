package com.trionesdev.commons.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Data
@SuperBuilder
@NoArgsConstructor
public class EnumOption {
    private String value;
    private String label;
    private Map<String, String> extra;

    public EnumOption(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public EnumOption(String value, String label, Map<String, String> extra) {
        this.value = value;
        this.label = label;
        this.extra = extra;
    }

    public static EnumOption of(String value, String label) {
        return new EnumOption(value, label);
    }
}
