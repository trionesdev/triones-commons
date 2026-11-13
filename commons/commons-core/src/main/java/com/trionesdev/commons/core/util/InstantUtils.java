package com.trionesdev.commons.core.util;

import java.time.Instant;
import java.time.ZoneId;

public class InstantUtils {
    public static Instant startTime(Instant instant) {
        if (instant == null) {
            return null;
        }
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDate().atStartOfDay(zoneId).toInstant();
    }

    public static Instant endTime(Instant instant) {
        if (instant == null) {
            return null;
        }
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDate().plusDays(1).atStartOfDay(zoneId).toInstant().minusNanos(1);
    }
}
