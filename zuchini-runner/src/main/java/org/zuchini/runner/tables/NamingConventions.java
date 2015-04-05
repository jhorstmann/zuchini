package org.zuchini.runner.tables;

import java.util.Locale;
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
        WORDS() {
            @Override
            public String toDisplayName(String property) {
                Pattern pattern = Pattern.compile("^[a-z]|(?<=[^A-Z])[A-Z]");
                Matcher matcher = pattern.matcher(property);
                StringBuffer sb = new StringBuffer();
                while (matcher.find()) {
                    String letter = matcher.group();
                    matcher.appendReplacement(sb, matcher.start() == 0 ? letter : " " + letter.toLowerCase(Locale.ROOT));
                }
                matcher.appendTail(sb);
                return sb.toString();
            }

            @Override
            public String toProperty(String displayName) {
                Pattern pattern = Pattern.compile("(?:^|\\s+)([a-z])");
                Matcher matcher = pattern.matcher(displayName);
                StringBuffer sb = new StringBuffer();
                while (matcher.find()) {
                    String letter = matcher.group(1);
                    matcher.appendReplacement(sb, matcher.start() == 0 ? letter : letter.toUpperCase(Locale.ROOT));
                }
                matcher.appendTail(sb);
                return sb.toString();
            }
        },
        TITLECASE() {
            @Override
            public String toDisplayName(String property) {
                Pattern pattern = Pattern.compile("^[a-z]|(?<=[^A-Z])[A-Z]");
                Matcher matcher = pattern.matcher(property);
                StringBuffer sb = new StringBuffer();
                while (matcher.find()) {
                    String letter = matcher.group().toUpperCase(Locale.ROOT);
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
                    matcher.appendReplacement(sb, matcher.start() == 0 ? letter.toLowerCase(Locale.ROOT) : letter);
                }
                matcher.appendTail(sb);
                return sb.toString();
            }
        }
    }
}
