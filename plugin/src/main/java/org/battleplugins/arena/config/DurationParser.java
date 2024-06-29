package org.battleplugins.arena.config;

import java.time.Duration;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DurationParser implements Function<Object, Duration> {
    private static final Pattern DURATION_PATTERN = Pattern.compile("(\\d+)([yYmMdDhHwWsS])");

    @Override
    public Duration apply(Object object) {
        if (object instanceof Number number) {
            return Duration.ofSeconds(number.longValue());
        }

        String value = object.toString();
        if (value.isBlank()) {
            return Duration.ZERO;
        }

        // Define a regular expression to match the duration format
        Matcher matcher = DURATION_PATTERN.matcher(value);

        long totalSeconds = 0;
        while (matcher.find()) {
            long amount = Long.parseLong(matcher.group(1));
            String unit = matcher.group(2);

            switch (unit) {
                case "y":
                    totalSeconds += amount * 365 * 24 * 60 * 60; // assuming 365 days in a year
                    break;
                case "M":
                    totalSeconds += amount * 30 * 24 * 60 * 60; // assuming 30 days in a month
                    break;
                case "w":
                    totalSeconds += amount * 7 * 24 * 60 * 60; // 7 days in a week
                    break;
                case "d":
                    totalSeconds += amount * 24 * 60 * 60; // 24 hours in a day
                    break;
                case "h":
                    totalSeconds += amount * 60 * 60; // 60 minutes in an hour
                    break;
                case "m":
                    totalSeconds += amount * 60; // 60 seconds in a minute
                    break;
                case "s":
                    totalSeconds += amount;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid unit: " + unit);
            }
        }

        return Duration.ofSeconds(totalSeconds);
    }
}
