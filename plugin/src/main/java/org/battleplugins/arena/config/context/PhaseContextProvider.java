package org.battleplugins.arena.config.context;

import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.competition.phase.CompetitionPhase;
import org.battleplugins.arena.competition.phase.CompetitionPhaseType;
import org.battleplugins.arena.config.ArenaConfigParser;
import org.battleplugins.arena.config.ArenaOption;
import org.battleplugins.arena.config.ParseException;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class PhaseContextProvider implements ContextProvider<Map<CompetitionPhaseType<?, ?>, CompetitionPhaseType.Provider<?, ?>>> {

    @Override
    public Map<CompetitionPhaseType<?, ?>, CompetitionPhaseType.Provider<?, ?>> provideInstance(ArenaOption option, Class<?> type, ConfigurationSection configuration, String name, @Nullable Object scope) {
        if (!Map.class.isAssignableFrom(type)) {
            throw new ParseException("Expected " + type.getName() + " to be assignable from Map when loading phases!");
        }

        ConfigurationSection configurationSection = configuration.getConfigurationSection(name);
        if (configurationSection == null) {
            throw new ParseException("Failed to find configuration section " + name + " in configuration section " + configuration.getName());
        }

        Map<CompetitionPhaseType<?, ?>, CompetitionPhaseType.Provider<?, ?>> phases = new HashMap<>();
        for (String key : configurationSection.getKeys(false)) {
            ConfigurationSection section = configurationSection.getConfigurationSection(key);
            if (section == null) {
                throw new ParseException("Failed to find configuration section " + key + " in configuration section " + configurationSection.getName());
            }

            CompetitionPhaseType<?, ?> phaseType = CompetitionPhaseType.get(key);
            if (phaseType == null) {
                throw new ParseException("Unrecognized phase detected (" + key + ") when loading configuration section " + configurationSection.getName());
            }

            phases.put(phaseType, genericHell(phaseType.getPhaseType(), section, phaseType));
        }

        return phases;
    }

    private static <C extends Competition<C>, T extends CompetitionPhase<C>> CompetitionPhaseType.Provider<C, T> genericHell(Class<T> phaseClassType, ConfigurationSection section, Object key) {
        return competition -> ArenaConfigParser.newInstance(phaseClassType, section, competition, key);
    }
}
