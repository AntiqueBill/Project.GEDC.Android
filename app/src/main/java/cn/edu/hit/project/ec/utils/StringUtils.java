package cn.edu.hit.project.ec.utils;

import java.util.regex.Matcher;

public class StringUtils {

    public static String escapeString(String s) {
        return s.replaceAll(Matcher.quoteReplacement("\\"), Matcher.quoteReplacement("\\\\\\\\"))
                .replaceAll("/", Matcher.quoteReplacement("\\\\/"));
    }
}
