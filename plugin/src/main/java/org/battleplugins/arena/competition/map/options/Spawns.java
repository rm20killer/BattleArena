package org.battleplugins.arena.competition.map.options;

import org.battleplugins.arena.config.ArenaOption;
import org.battleplugins.arena.util.PositionWithRotation;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Represents the spawn options for a map.
 */
public class Spawns {
    @ArenaOption(name = "waitroom", description = "The waitroom spawn.")
    private PositionWithRotation waitroomSpawn;

    @ArenaOption(name = "spectator", description = "The spectator spawn.")
    private PositionWithRotation spectatorSpawn;

    @ArenaOption(name = "team-spawns", description = "The spawns options for each team.")
    private Map<String, TeamSpawns> teamSpawns;

    public Spawns() {
    }

    public Spawns(@Nullable PositionWithRotation waitroomSpawn, @Nullable PositionWithRotation spectatorSpawn, @Nullable Map<String, TeamSpawns> teamSpawns) {
        this.waitroomSpawn = waitroomSpawn;
        this.spectatorSpawn = spectatorSpawn;
        this.teamSpawns = teamSpawns;
    }

    @Nullable
    public final PositionWithRotation getWaitroomSpawn() {
        return this.waitroomSpawn;
    }

    @Nullable
    public final PositionWithRotation getSpectatorSpawn() {
        return this.spectatorSpawn;
    }

    @Nullable
    public final Map<String, TeamSpawns> getTeamSpawns() {
        return this.teamSpawns;
    }

    public final int getSpawnPointCount() {
        if (this.teamSpawns == null) {
            return 0;
        }

        int count = 0;
        for (TeamSpawns spawns : this.teamSpawns.values()) {
            if (spawns.getSpawns() != null) {
                count += spawns.getSpawns().size();
            }
        }

        return count;
    }

    public final int getSpawnPointCount(String teamName) {
        if (this.teamSpawns == null) {
            return 0;
        }

        int count = 0;
        TeamSpawns spawns = this.teamSpawns.get(teamName);
        if (spawns != null && spawns.getSpawns() != null) {
            count = spawns.getSpawns().size();
        }

        return count;
    }
}
