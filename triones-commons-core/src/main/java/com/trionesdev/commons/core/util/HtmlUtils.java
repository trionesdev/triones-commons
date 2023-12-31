package com.trionesdev.commons.core.util;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;

public class HtmlUtils {

    private static final Safelist relaxedWhitelist = Safelist.relaxed();

    static Document.OutputSettings relaxedOutputSettings = new Document.OutputSettings().prettyPrint(false);

    static {
        relaxedWhitelist.addAttributes(":all", "style");
    }

    /**
     * xxs攻击内容去除
     *
     * @param content
     * @return
     */
    public static String xxsClean(String content) {
        if (StringUtils.isBlank(content)) {
            return content;
        }
        return Jsoup.clean(content, "", relaxedWhitelist, relaxedOutputSettings);
    }
}
