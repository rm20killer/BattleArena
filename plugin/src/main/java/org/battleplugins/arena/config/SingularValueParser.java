package org.battleplugins.arena.config;

import java.util.ArrayDeque;
import java.util.Queue;

public final class SingularValueParser {

    public static ArgumentBuffer parseNamed(String contents, BraceStyle style, char separator) throws ParseException {
        return parse(contents, style, separator, true);
    }

    public static ArgumentBuffer parseUnnamed(String contents, BraceStyle style, char separator) throws ParseException {
        return parse(contents, style, separator, false);
    }

    private static ArgumentBuffer parse(String contents, BraceStyle style, char separator, boolean named) throws ParseException {
        ArgumentBuffer buffer = new ArgumentBuffer();
        if (named) {
            String[] split = contents.split(style.open);
            String dataType = split[0];

            buffer.push("root", dataType);

            if (split.length == 1) {
                // No more data to parse
                return buffer;
            }

            return parseNamed(buffer, split[1], style, separator);
        } else {
            return parseUnnamed(buffer, contents, style, separator);
        }
    }

    private static ArgumentBuffer parseNamed(ArgumentBuffer buffer, String contents, BraceStyle style, char separator) throws ParseException {
        contents = contents.replace(style.close, "");
        for (String argument : contents.split(Character.toString(separator))) {
            String[] optionSplit = argument.split("=");
            if (optionSplit.length != 2) {
                throw new ParseException("Invalid argument length! Expected arguments in the form of " +
                        "<key>=<value>, but got " + argument);
            }

            buffer.push(optionSplit[0], optionSplit[1]);
        }

        return buffer;
    }

    private static ArgumentBuffer parseUnnamed(ArgumentBuffer buffer, String contents, BraceStyle style, char separator) throws ParseException {
        contents = contents.substring(1);
        contents = contents.substring(0, contents.length() - 1);

        int index = 0;
        for (String argument : contents.split(Character.toString(separator))) {
            buffer.push(Integer.toString(index++), argument);
        }

        return buffer;
    }

    public static final class ArgumentBuffer {
        private final Queue<Argument> values = new ArrayDeque<>();

        public void push(String key, String value) {
            this.values.add(new Argument(key, value));
        }

        public boolean hasNext() {
            return !this.values.isEmpty();
        }

        public Argument pop() {
            return this.values.poll();
        }
    }

    public record Argument(String key, String value) {
    }

    public enum BraceStyle {
        CURLY("\\{", "}"),
        SQUARE("\\[", "]"),
        ROUND("\\(", ")");

        private final String open;
        private final String close;

        BraceStyle(String open, String close) {
            this.open = open;
            this.close = close;
        }
    }
}
