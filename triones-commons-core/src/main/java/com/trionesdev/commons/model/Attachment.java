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
    private String type;
    private String name;
    private String imageUrl;
    private String url;
    private String extension;
}
