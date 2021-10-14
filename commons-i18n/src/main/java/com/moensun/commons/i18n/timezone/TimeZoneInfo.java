package com.moensun.commons.i18n.timezone;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TimeZoneInfo implements Serializable {
    private String code;
    private String region;
    private String regionName;
    private String timeZoneName;
    private String displayName;
}
