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
        LOWERCASE_WORDS() {
            private final Pattern PROPERTY_PATTERN = Pattern.compile("^[a-z]|(?<=[^A-Z])[A-Z]");
            private final Pattern NAME_PATTERN = Pattern.compile("(?:^|\\s+)([a-z])");

            @Override
            public String toDisplayName(String property) {
                final Matcher matcher = PROPERTY_PATTERN.matcher(property);
                final StringBuffer sb = new StringBuffer();
                while (matcher.find()) {
                    final String letter = matcher.group();
                    matcher.appendReplacement(sb, matcher.start() == 0 ? letter : " " + letter.toLowerCase(Locale.ROOT));
                }
                matcher.appendTail(sb);
                return sb.toString();
            }

            @Override
            public String toProperty(String displayName) {
                final Matcher matcher = NAME_PATTERN.matcher(displayName);
                final StringBuffer sb = new StringBuffer();
                while (matcher.find()) {
                    String letter = matcher.group(1);
                    matcher.appendReplacement(sb, matcher.start() == 0 ? letter : letter.toUpperCase(Locale.ROOT));
                }
                matcher.appendTail(sb);
                return sb.toString();
            }
        },
        UPPERCASE_WORDS() {
            private final Pattern PROPERTY_PATTERN = Pattern.compile("^[a-z]|(?<=[^A-Z])[A-Z]");
            private final Pattern NAME_PATTERN = Pattern.compile("(?:^|\\s+)([A-Z])");

            @Override
            public String toDisplayName(String property) {
                final Matcher matcher = PROPERTY_PATTERN.matcher(property);
                final StringBuffer sb = new StringBuffer();
                while (matcher.find()) {
                    final String letter = matcher.group().toUpperCase(Locale.ROOT);
                    matcher.appendReplacement(sb, matcher.start() == 0 ? letter : " " + letter);
                }
                matcher.appendTail(sb);
                return sb.toString();
            }

            @Override
            public String toProperty(String displayName) {
                final Matcher matcher = NAME_PATTERN.matcher(displayName);
                final StringBuffer sb = new StringBuffer();
                while (matcher.find()) {
                    final String letter = matcher.group(1);
                    matcher.appendReplacement(sb, matcher.start() == 0 ? letter.toLowerCase(Locale.ROOT) : letter);
                }
                matcher.appendTail(sb);
                return sb.toString();
            }
        }
    }
}
