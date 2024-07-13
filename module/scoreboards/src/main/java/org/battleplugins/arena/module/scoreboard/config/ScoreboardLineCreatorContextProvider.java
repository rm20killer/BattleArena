package org.battleplugins.arena.module.scoreboard.config;

import org.battleplugins.arena.config.ArenaConfigParser;
import org.battleplugins.arena.config.ArenaOption;
import org.battleplugins.arena.config.ParseException;
import org.battleplugins.arena.config.context.ContextProvider;
import org.battleplugins.arena.module.scoreboard.line.ScoreboardLineCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScoreboardLineCreatorContextProvider implements ContextProvider<List<ScoreboardLineCreator>> {

    @SuppressWarnings("unchecked")
    @Override
    public List<ScoreboardLineCreator> provideInstance(@Nullable Path sourceFile, ArenaOption option, Class<?> type, ConfigurationSection configuration, String name, @Nullable Object scope) throws ParseException {
        if (!List.class.isAssignableFrom(type)) {
            throw new ParseException("Expected " + type.getName() + " to be assignable from List when loading scoreboard lines!")
                    .context("Type", type.getName())
                    .context("Name", name)
                    .context("Section", configuration.getName())
                    .cause(ParseException.Cause.INVALID_TYPE)
                    .type(ScoreboardLineCreator.class)
                    .sourceFile(sourceFile);
        }

        List<ScoreboardLineCreator> lineCreators = new ArrayList<>();

        List<?> list = configuration.getList(name);
        for (Object object : list) {
            if (!(object instanceof Map<?, ?> map)) {
                throw new ParseException("Expected " + name + " to be a list of maps when loading scoreboard lines!")
                        .context("Type", type.getName())
                        .context("Name", name)
                        .context("Section", configuration.getName())
                        .cause(ParseException.Cause.INVALID_TYPE)
                        .type(ScoreboardLineCreator.class)
                        .sourceFile(sourceFile);
            }

            if (map.size() != 1) {
                throw new ParseException("Expected " + name + " to be a list of maps with a single key when loading scoreboard lines!")
                        .context("Type", type.getName())
                        .context("Name", name)
                        .context("Section", configuration.getName())
                        .cause(ParseException.Cause.INVALID_TYPE)
                        .type(ScoreboardLineCreator.class)
                        .sourceFile(sourceFile);
            }

            Map.Entry<?, ?> iterator = map.entrySet().iterator().next();
            String key = iterator.getKey().toString();
            Class<? extends ScoreboardLineCreator> lineCreator = ScoreboardLineCreator.LINE_CREATORS.get(key);
            if (lineCreator == null) {
                throw new ParseException("Failed to find line creator for " + key + " in configuration section " + key)
                        .context("Type", type.getName())
                        .context("Name", name)
                        .context("Section", configuration.getName())
                        .cause(ParseException.Cause.MISSING_SECTION)
                        .type(ScoreboardLineCreator.class)
                        .userError()
                        .sourceFile(sourceFile);
            }

            Object value = iterator.getValue();
            if (!(value instanceof Map<?, ?>)) {
                throw new ParseException("Expected " + name + " to be a list of maps with a single key and a map value when loading scoreboard lines!")
                        .context("Type", type.getName())
                        .context("Name", name)
                        .context("Section", configuration.getName())
                        .cause(ParseException.Cause.INVALID_TYPE)
                        .type(ScoreboardLineCreator.class)
                        .sourceFile(sourceFile);
            }

            ConfigurationSection section = toMemorySection((Map<String, Object>) value);
            ScoreboardLineCreator scoreboardLineCreator = ArenaConfigParser.newInstance(sourceFile, lineCreator, section);

            lineCreators.add(scoreboardLineCreator);
        }

        return lineCreators;
    }

    private static ConfigurationSection toMemorySection(Map<String, Object> map) {
        MemoryConfiguration memoryConfig = new MemoryConfiguration();
        memoryConfig.addDefaults(map);
        return memoryConfig;
    }
}
