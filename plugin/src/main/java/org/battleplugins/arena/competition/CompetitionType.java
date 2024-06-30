package org.battleplugins.arena.competition;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a competition type.
 */
public final class CompetitionType {
    private static final Map<String, CompetitionType> COMPETITION_TYPES = new HashMap<>();

    public static final CompetitionType EVENT = new CompetitionType("Event");
    public static final CompetitionType MATCH = new CompetitionType("Match");

    private final String name;

    CompetitionType(String name) {
        this.name = name;

        COMPETITION_TYPES.put(name, this);
    }

    @Nullable
    public static CompetitionType get(String name) {
        return COMPETITION_TYPES.get(name);
    }
}
