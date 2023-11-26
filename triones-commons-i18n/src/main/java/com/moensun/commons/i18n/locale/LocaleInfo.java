package com.moensun.commons.i18n.locale;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LocaleInfo implements Serializable {

    private String code;
    private String language;
    private String country;
    private String displayName;
}
