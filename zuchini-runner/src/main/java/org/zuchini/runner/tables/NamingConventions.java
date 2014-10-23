package org.zuchini.runner.tables;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NamingConventions {
    public static enum DefaultNamingConventions implements NamingConvention {
        IDENTITY() {
            @Override
            public String toDisplayName(String property) {
                return property;
            }

            @Override
            public String toProperty(String displayName) {
                return displayName;
            }
        },
        UPPERCASE_FIRST() {
            @Override
            public String toDisplayName(String property) {
                return property.substring(0, 1).toUpperCase() + property.substring(1);
            }

            @Override
            public String toProperty(String displayName) {
                return displayName.substring(0, 1).toLowerCase() + displayName.substring(1);
            }
        },
        TITLECASE() {
            @Override
            public String toDisplayName(String property) {
                Pattern pattern = Pattern.compile("^[a-z]|(?<=[^A-Z])[A-Z]");
                Matcher matcher = pattern.matcher(property);
                StringBuffer sb = new StringBuffer();
                while (matcher.find()) {
                    String letter = matcher.group().toUpperCase();
                    matcher.appendReplacement(sb, matcher.start() == 0 ? letter : " " + letter);
                }
                matcher.appendTail(sb);
                return sb.toString();
            }

            @Override
            public String toProperty(String displayName) {
                Pattern pattern = Pattern.compile("(?:^|\\s+)([A-Z])");
                Matcher matcher = pattern.matcher(displayName);
                StringBuffer sb = new StringBuffer();
                while (matcher.find()) {
                    String letter = matcher.group(1);
                    matcher.appendReplacement(sb, matcher.start() == 0 ? letter.toLowerCase() : letter);
                }
                matcher.appendTail(sb);
                return sb.toString();
            }
        }
    }
}
