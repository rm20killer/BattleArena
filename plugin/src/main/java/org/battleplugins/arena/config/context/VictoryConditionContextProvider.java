package org.battleplugins.arena.config.context;

import org.battleplugins.arena.competition.LiveCompetition;
import org.battleplugins.arena.competition.victory.VictoryCondition;
import org.battleplugins.arena.competition.victory.VictoryConditionType;
import org.battleplugins.arena.config.ArenaConfigParser;
import org.battleplugins.arena.config.ArenaOption;
import org.battleplugins.arena.config.ParseException;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class VictoryConditionContextProvider implements ContextProvider<Map<VictoryConditionType<?, ?>, VictoryConditionType.Provider<?, ?>>> {

    @Override
    public Map<VictoryConditionType<?, ?>, VictoryConditionType.Provider<?, ?>> provideInstance(ArenaOption option, Class<?> type, ConfigurationSection configuration, String name, @Nullable Object scope) {
        if (!Map.class.isAssignableFrom(type)) {
            throw new ParseException("Expected " + type.getName() + " to be assignable from Map when loading victory conditions!");
        }

        ConfigurationSection configurationSection = configuration.getConfigurationSection(name);
        if (option.required() && configurationSection == null) {
            throw new ParseException("Failed to find configuration section " + name + " in configuration section " + configuration.getName());
        } else if (!option.required() && configurationSection == null) {
            return Map.of();
        }

        Map<VictoryConditionType<?, ?>, VictoryConditionType.Provider<?, ?>> victoryConditions = new HashMap<>();
        for (String key : configurationSection.getKeys(false)) {
            ConfigurationSection section = configurationSection.getConfigurationSection(key);
            if (section == null) {
                throw new ParseException("Failed to find configuration section " + key + " in configuration section " + configurationSection.getName());
            }

            VictoryConditionType<?, ?> conditionType = VictoryConditionType.get(key);
            if (conditionType == null) {
                throw new ParseException("Unrecognized victory condition detected (" + key + ") when loading configuration section " + configurationSection.getName());
            }

            victoryConditions.put(conditionType, genericHell(conditionType.getVictoryType(), section, conditionType));
        }

        return victoryConditions;
    }

    private static <C extends LiveCompetition<C>, T extends VictoryCondition<C>> VictoryConditionType.Provider<C, T> genericHell(Class<T> victoryClassType, ConfigurationSection section, Object key) {
        return competition -> ArenaConfigParser.newInstance(victoryClassType, section, competition, key);
    }
}
