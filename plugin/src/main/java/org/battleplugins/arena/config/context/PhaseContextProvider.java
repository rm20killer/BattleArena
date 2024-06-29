package org.battleplugins.arena.config.context;

import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.competition.phase.CompetitionPhase;
import org.battleplugins.arena.competition.phase.CompetitionPhaseType;
import org.battleplugins.arena.config.ArenaConfigParser;
import org.battleplugins.arena.config.ArenaOption;
import org.battleplugins.arena.config.ParseException;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class PhaseContextProvider implements ContextProvider<Map<CompetitionPhaseType<?, ?>, CompetitionPhaseType.Provider<?, ?>>> {

    @Override
    public Map<CompetitionPhaseType<?, ?>, CompetitionPhaseType.Provider<?, ?>> provideInstance(@Nullable Path sourceFile, ArenaOption option, Class<?> type, ConfigurationSection configuration, String name, @Nullable Object scope) throws ParseException {
        if (!Map.class.isAssignableFrom(type)) {
            throw new ParseException("Expected " + type.getName() + " to be assignable from Map when loading phases!")
                    .context("Type", type.getName())
                    .context("Name", name)
                    .context("Section", configuration.getName())
                    .cause(ParseException.Cause.INVALID_TYPE)
                    .type(CompetitionPhaseType.class)
                    .sourceFile(sourceFile);
        }

        ConfigurationSection configurationSection = configuration.getConfigurationSection(name);
        if (configurationSection == null) {
            throw new ParseException("Failed to find configuration section " + name + " in configuration section " + configuration.getName())
                    .context("Type", type.getName())
                    .context("Name", name)
                    .context("Section", configuration.getName())
                    .cause(ParseException.Cause.MISSING_SECTION)
                    .type(CompetitionPhaseType.class)
                    .userError()
                    .sourceFile(sourceFile);
        }

        Map<CompetitionPhaseType<?, ?>, CompetitionPhaseType.Provider<?, ?>> phases = new HashMap<>();
        for (String key : configurationSection.getKeys(false)) {
            ConfigurationSection section = configurationSection.getConfigurationSection(key);
            if (section == null) {
                throw new ParseException("Failed to find configuration section " + key + " in configuration section " + configurationSection.getName())
                        .context("Type", type.getName())
                        .context("Name", key)
                        .context("Section", configurationSection.getName())
                        .cause(ParseException.Cause.MISSING_SECTION)
                        .type(CompetitionPhaseType.class)
                        .userError()
                        .sourceFile(sourceFile);
            }

            CompetitionPhaseType<?, ?> phaseType = CompetitionPhaseType.get(key);
            if (phaseType == null) {
                throw new ParseException("Unrecognized phase detected (" + key + ") when loading configuration section " + configurationSection.getName())
                        .context("Section", configuration.getName())
                        .context("Provided phase", key)
                        .context("Valid phases", CompetitionPhaseType.values().stream().map(CompetitionPhaseType::getName).toList().toString())
                        .cause(ParseException.Cause.INVALID_VALUE)
                        .type(CompetitionPhaseType.class)
                        .userError()
                        .sourceFile(sourceFile);
            }

            phases.put(phaseType, genericHell(sourceFile, phaseType.getPhaseType(), section, phaseType));
        }

        return phases;
    }

    private static <C extends Competition<C>, T extends CompetitionPhase<C>> CompetitionPhaseType.Provider<C, T> genericHell(@Nullable Path sourceFile, Class<T> phaseClassType, ConfigurationSection section, Object key) {
        return competition -> {
            try {
                return ArenaConfigParser.newInstance(phaseClassType, section, competition, key);
            } catch (ParseException e) {
                ParseException.handle(e.sourceFile(sourceFile));

                throw new RuntimeException("Failed to create phase " + key, e);
            }
        };
    }
}
