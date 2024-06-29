package org.battleplugins.arena.config.context;

import org.battleplugins.arena.competition.LiveCompetition;
import org.battleplugins.arena.competition.victory.VictoryCondition;
import org.battleplugins.arena.competition.victory.VictoryConditionType;
import org.battleplugins.arena.config.ArenaConfigParser;
import org.battleplugins.arena.config.ArenaOption;
import org.battleplugins.arena.config.ParseException;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class VictoryConditionContextProvider implements ContextProvider<Map<VictoryConditionType<?, ?>, VictoryConditionType.Provider<?, ?>>> {

    @Override
    public Map<VictoryConditionType<?, ?>, VictoryConditionType.Provider<?, ?>> provideInstance(@Nullable Path sourceFile, ArenaOption option, Class<?> type, ConfigurationSection configuration, String name, @Nullable Object scope) throws ParseException {
        if (!Map.class.isAssignableFrom(type)) {
            throw new ParseException("Expected " + type.getName() + " to be assignable from Map when loading victory conditions!")
                    .context("Type", type.getName())
                    .context("Name", name)
                    .context("Section", configuration.getName())
                    .cause(ParseException.Cause.INVALID_TYPE)
                    .type(VictoryConditionType.class)
                    .sourceFile(sourceFile);
        }

        ConfigurationSection configurationSection = configuration.getConfigurationSection(name);
        if (option.required() && configurationSection == null) {
            throw new ParseException("Failed to find configuration section " + name + " in configuration section " + configuration.getName())
                    .context("Type", type.getName())
                    .context("Name", name)
                    .context("Section", configuration.getName())
                    .cause(ParseException.Cause.MISSING_SECTION)
                    .type(VictoryConditionType.class)
                    .userError()
                    .sourceFile(sourceFile);
        } else if (!option.required() && configurationSection == null) {
            return Map.of();
        }

        Map<VictoryConditionType<?, ?>, VictoryConditionType.Provider<?, ?>> victoryConditions = new HashMap<>();
        for (String key : configurationSection.getKeys(false)) {
            ConfigurationSection section = configurationSection.getConfigurationSection(key);
            if (section == null) {
                throw new ParseException("Failed to find configuration section " + key + " in configuration section " + configurationSection.getName())
                        .context("Type", type.getName())
                        .context("Name", key)
                        .context("Section", configurationSection.getName())
                        .cause(ParseException.Cause.MISSING_SECTION)
                        .type(VictoryConditionType.class)
                        .userError()
                        .sourceFile(sourceFile);
            }

            VictoryConditionType<?, ?> conditionType = VictoryConditionType.get(key);
            if (conditionType == null) {
                throw new ParseException("Unrecognized victory condition detected (" + key + ") when loading configuration section " + configurationSection.getName())
                        .context("Section", configuration.getName())
                        .context("Provided condition", key)
                        .context("Valid conditions", VictoryConditionType.values().stream().map(VictoryConditionType::getName).toList().toString())
                        .cause(ParseException.Cause.INVALID_VALUE)
                        .type(VictoryConditionType.class)
                        .userError()
                        .sourceFile(sourceFile);
            }

            victoryConditions.put(conditionType, genericHell(sourceFile, conditionType.getVictoryType(), section, conditionType));
        }

        return victoryConditions;
    }

    private static <C extends LiveCompetition<C>, T extends VictoryCondition<C>> VictoryConditionType.Provider<C, T> genericHell(@Nullable Path sourceFile, Class<T> victoryClassType, ConfigurationSection section, Object key) {
        return competition -> {
            try {
                return ArenaConfigParser.newInstance(victoryClassType, section, competition, key);
            } catch (ParseException e) {
                ParseException.handle(e.sourceFile(sourceFile));

                throw new RuntimeException("Failed to create victory condition " + key, e);
            }
        };
    }
}
