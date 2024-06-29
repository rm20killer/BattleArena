package org.battleplugins.arena.config.context;

import org.battleplugins.arena.config.ParseException;
import org.battleplugins.arena.config.SingularValueParser;
import org.battleplugins.arena.options.ArenaOption;
import org.battleplugins.arena.options.ArenaOptionType;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OptionContextProvider implements ContextProvider<Map<ArenaOptionType<?>, ArenaOption>> {

    @Override
    public Map<ArenaOptionType<?>, ArenaOption> provideInstance(@Nullable Path sourceFile, org.battleplugins.arena.config.ArenaOption option, Class<?> type, ConfigurationSection configuration, String name, @Nullable Object scope) throws ParseException {
        if (!Map.class.isAssignableFrom(type)) {
            throw new ParseException("Expected " + type.getName() + " to be assignable from Map when loading events!")
                    .context("Type", type.getName())
                    .context("Name", name)
                    .context("Section", configuration.getName())
                    .cause(ParseException.Cause.INVALID_TYPE)
                    .type(ArenaOptionType.class)
                    .sourceFile(sourceFile);
        }

        if (!configuration.contains(name)) {
            return Map.of();
        }

        Map<ArenaOptionType<?>, ArenaOption> arenaOptions = new LinkedHashMap<>();
        List<String> options = configuration.getStringList(name);
        for (String optionStr : options) {
            SingularValueParser.ArgumentBuffer buffer = SingularValueParser.parseNamed(optionStr, SingularValueParser.BraceStyle.CURLY, ';');
            if (!buffer.hasNext()) {
                throw new ParseException("No data found for arena option")
                        .context("Section", configuration.getName())
                        .context("Provided options", options.toString())
                        .cause(ParseException.Cause.INVALID_VALUE)
                        .type(ArenaOptionType.class)
                        .userError()
                        .sourceFile(sourceFile);
            }

            SingularValueParser.Argument root = buffer.pop();
            if (!root.key().equals("root")) {
                throw new ParseException("Expected root key for ArenaOption, got " + root.key())
                        .context("Section", configuration.getName())
                        .context("Provided key", root.key())
                        .cause(ParseException.Cause.INTERNAL_ERROR)
                        .type(ArenaOptionType.class)
                        .sourceFile(sourceFile);
            }

            ArenaOptionType<?> optionType = ArenaOptionType.get(root.value());
            if (optionType == null) {
                throw new ParseException("Unrecognized arena option detected (" + root.value() + ") when loading configuration section " + configuration.getName())
                        .context("Section", configuration.getName())
                        .context("Provided options", options.toString())
                        .context("Valid options", ArenaOptionType.values().stream().map(ArenaOptionType::getName).toList().toString())
                        .cause(ParseException.Cause.INTERNAL_ERROR)
                        .type(ArenaOptionType.class)
                        .userError()
                        .sourceFile(sourceFile);
            }

            Map<String, String> params = new LinkedHashMap<>();
            while (buffer.hasNext()) {
                SingularValueParser.Argument argument = buffer.pop();
                params.put(argument.key(), argument.value());
            }

            try {
                ArenaOption arenaOption = optionType.create(params);
                arenaOptions.put(optionType, arenaOption);
            } catch (IllegalArgumentException e) {
                throw new ParseException("Failed to create event action " + root.key() + " with params " + params, e);
            }
        }

        return arenaOptions;
    }
}
