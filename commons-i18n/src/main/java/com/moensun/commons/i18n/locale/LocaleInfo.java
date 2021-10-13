package com.moensun.commons.i18n.locale;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LocaleInfo {
    private String code;
    private String language;
    private String country;
    private String displayName;
}
