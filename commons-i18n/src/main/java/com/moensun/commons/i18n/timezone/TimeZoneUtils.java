package com.moensun.commons.i18n.timezone;

import com.ibm.icu.impl.ZoneMeta;
import com.ibm.icu.text.TimeZoneFormat;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.ULocale;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;

import java.util.*;
import java.util.stream.Collectors;

public class TimeZoneUtils {

    public static List<TimeZoneInfo> getDisplayNames(Set<String> zoneIds,Locale locale){
        if(CollectionUtils.isEmpty(zoneIds)){
            return Collections.emptyList();
        }
        ULocale uLocale = ULocale.forLocale(locale);
        TimeZoneFormat timeZoneFormat = TimeZoneFormat.getInstance(uLocale);
        MultiValuedMap<String,String> zoneMultiValuedMap = new HashSetValuedHashMap<>();
        List<TimeZoneInfo> timeZoneInfos = new ArrayList<>();
        zoneIds.forEach(t->{
          String region = ZoneMeta.getCanonicalCountry(t);
            zoneMultiValuedMap.put(region,t);
           String regionName = (new ULocale("und", region)).getDisplayCountry(uLocale);
            timeZoneInfos.add(TimeZoneInfo.builder().code(t).region(region).regionName(regionName).build());
        });
        return timeZoneInfos.stream().map(timeZoneInfo -> {
            TimeZone timeZone = TimeZone.getTimeZone(timeZoneInfo.getCode());
            if(timeZoneInfo.getCode().startsWith("Etc/")){
                timeZoneInfo.setDisplayName(timeZone.getDisplayName(uLocale));
                return timeZoneInfo;
            }
            String extended = timeZoneFormat.formatOffsetISO8601Extended(timeZone.getRawOffset(),false,false,false);
            StringBuilder sb = new StringBuilder();
            Collection<String> regionZones = zoneMultiValuedMap.get(timeZoneInfo.getRegion());
            sb.append(timeZoneInfo.getRegionName());
            if(CollectionUtils.isNotEmpty(regionZones) && regionZones.size()>1){
                sb.append(":").append(timeZone.getDisplayName(uLocale));
            }
            sb.append("(").append(extended).append(")");
            timeZoneInfo.setDisplayName(sb.toString());
            return timeZoneInfo;
        }).sorted(Comparator.comparing(TimeZoneInfo::getDisplayName).reversed()).collect(Collectors.toList());
    }

    public static List<TimeZoneInfo> getDisplayNames(Locale locale){
        return getDisplayNames(SetUtils.hashSet(TimeZone.getAvailableIDs()),locale);
    }

}
