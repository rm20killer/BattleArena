package org.battleplugins.arena.competition.map.options;

import org.battleplugins.arena.config.ArenaOption;
import org.battleplugins.arena.util.PositionWithRotation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represents the spawn options for a team.
 */
public class TeamSpawns {

    @ArenaOption(name = "spawns", description = "The spawns for this team.")
    private List<PositionWithRotation> spawns;

    public TeamSpawns() {
    }

    public TeamSpawns(@Nullable List<PositionWithRotation> spawns) {
        this.spawns = spawns;
    }

    @Nullable
    public final List<PositionWithRotation> getSpawns() {
        return this.spawns;
    }
}
