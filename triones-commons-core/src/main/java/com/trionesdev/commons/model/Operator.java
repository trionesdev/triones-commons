package com.trionesdev.commons.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Operator {
    private String id;
    private String role;
    private String name;
    private String avatar;
    private String gender;
}
