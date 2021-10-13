package com.moensun.commons.i18n.timezone;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TimeZoneInfo {
    private String code;
    private String region;
    private String regionName;
    private String timeZoneName;
    private String displayName;
}
