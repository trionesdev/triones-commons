package com.moensun.commons.core.util;

import java.util.Calendar;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

public class FilePathUtils {
    private FilePathUtils() {
    }

    public static String extension(String filename) {
        if (StringUtils.isEmpty(filename)) {
            return "";
        } else {
            int pointIndex = filename.lastIndexOf('.');
            if (pointIndex > -1) {
                return filename.substring(pointIndex);
            } else {
                int lineIndex = filename.lastIndexOf('/');
                if (lineIndex > -1) {
                    return "."+filename.substring(lineIndex + 1);
                } else {
                    return "";
                }
            }
        }
    }

    public static String randomFilename(String fileName) {
        return RandomStringUtils.randomAlphabetic(32) + extension(fileName);
    }

    public static String originalNameDatePath(String fileName) {
        return datePath() + "/" + fileName;
    }

    public static String randomNameDatePath(String fileName) {
        return datePath() + "/" + randomFilename(fileName);
    }

    public static String datePath() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar
                .get(Calendar.DATE);
    }

    public static String removeDomain(String path, String prefix) {
        if (StringUtils.isEmpty(path)) {
            return "";
        }
        if (StringUtils.isBlank(prefix)) {
            return path;
        }
        if (!prefix.startsWith("http") && !prefix.startsWith("https")) {
            path = path.replaceAll("^https?://" + prefix, "");
        } else {
            path = path.replaceAll(prefix, "");
        }
        path = path.replaceAll("^/", "");
        return path;
    }

    public static String joinDomain(String path, String domain) {
        if (StringUtils.isEmpty(path)) {
            return "";
        }
        if (StringUtils.isBlank(domain)) {
            return path;
        }
        if (!path.startsWith("http") && !path.startsWith("https")) {
            if (domain.endsWith("://")) {
                return domain + path;
            } else if (!domain.endsWith("/") && !path.startsWith("/")) {
                return domain + "/" + path;
            } else {
                return domain + path;
            }
        }
        return path;
    }
}
