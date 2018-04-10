package net.soti.go.plugin.notification.email.model;

/**
 * User: wsim
 * Date: 2018-04-10
 */
public enum SearchFilterTypes {
    FullText, StartsWith, EndsWith, Contains, None, Unknown;

    public static SearchFilterTypes findType(String text) {
        if (text == null || text.length() == 0) {
            return None;
        }

        if (!text.contains("*")) {
            return FullText;
        }

        if (text.equals("*")) {
            return None;
        }

        if (text.startsWith("*")) {
            int wildcardIndex = text.substring(1, text.length() - 1).indexOf("*");
            if (wildcardIndex >= 0 && wildcardIndex != text.length() - 2) {
                return Unknown;
            }

            if (text.endsWith("*")) {
                return Contains;
            }
            return EndsWith;
        }

        if (text.endsWith("*")) {
            return StartsWith;
        }

        return Unknown;
    }

    public static String getKeyword(String text) {
        if (text == null || text.length() == 0) {
            return text;
        }

        return text.replaceAll("\\*", "");
    }
}
