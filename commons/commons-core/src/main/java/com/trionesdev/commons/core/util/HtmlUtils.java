package com.trionesdev.commons.core.util;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;

public class HtmlUtils {

    private static final Safelist RELAXED_SAFELIST = Safelist.relaxed()
            .addAttributes(":all", "style");

    private static final Document.OutputSettings RELAXED_OUTPUT_SETTINGS =
            new Document.OutputSettings().prettyPrint(false);

    private HtmlUtils() {
    }

    /**
     * 清除 XSS 攻击内容
     *
     * @param content
     * @return
     */
    public static String xssClean(String content) {
        if (StringUtils.isBlank(content)) {
            return content;
        }
        return Jsoup.clean(content, "", RELAXED_SAFELIST, RELAXED_OUTPUT_SETTINGS);
    }

    /**
     * @deprecated 拼写错误，请使用 {@link #xssClean(String)}
     */
    @Deprecated
    public static String xxsClean(String content) {
        return xssClean(content);
    }
}
