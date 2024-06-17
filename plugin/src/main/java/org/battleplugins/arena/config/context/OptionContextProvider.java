package org.battleplugins.arena.config.context;

import org.battleplugins.arena.config.ParseException;
import org.battleplugins.arena.config.SingularValueParser;
import org.battleplugins.arena.options.ArenaOption;
import org.battleplugins.arena.options.ArenaOptionType;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OptionContextProvider implements ContextProvider<Map<ArenaOptionType<?>, ArenaOption>> {

    @Override
    public Map<ArenaOptionType<?>, ArenaOption> provideInstance(org.battleplugins.arena.config.ArenaOption option, Class<?> type, ConfigurationSection configuration, String name, @Nullable Object scope) {
        if (!Map.class.isAssignableFrom(type)) {
            throw new ParseException("Expected " + type.getName() + " to be assignable from Map when loading events!");
        }

        if (!configuration.contains(name)) {
            return Map.of();
        }

        Map<ArenaOptionType<?>, ArenaOption> arenaOptions = new LinkedHashMap<>();
        List<String> options = configuration.getStringList(name);
        for (String optionStr : options) {
            SingularValueParser.ArgumentBuffer buffer = SingularValueParser.parseNamed(optionStr, SingularValueParser.BraceStyle.CURLY, ';');
            if (!buffer.hasNext()) {
                throw new ParseException("No data found for ArenaOption");
            }

            SingularValueParser.Argument root = buffer.pop();
            if (!root.key().equals("root")) {
                throw new ParseException("Expected root key for ArenaOption, got " + root.key());
            }
            ArenaOptionType<?> optionType = ArenaOptionType.get(root.value());
            if (optionType == null) {
                throw new ParseException("Unrecognized arena option detected (" + root.value() + ") when loading configuration section " + configuration.getName());
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
