package com.trionesdev.commons.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Attachment {
    private String id;
    private String type;
    private String name;
    private String poster;
    private String url;
    private String extension;
}
