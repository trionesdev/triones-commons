package com.trionesdev.commons.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TrionesError implements Serializable {
    private static final long serialVersionUID = -1926135926270553351L;
    private String code;
    private String message;
}
