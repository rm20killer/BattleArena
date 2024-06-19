package org.battleplugins.arena.competition.map;

import net.kyori.adventure.util.TriState;
import org.battleplugins.arena.Arena;
import org.battleplugins.arena.ArenaLike;
import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.competition.CompetitionType;
import org.battleplugins.arena.competition.map.options.Bounds;
import org.battleplugins.arena.competition.map.options.Spawns;
import org.battleplugins.arena.config.ArenaOption;
import org.battleplugins.arena.config.PostProcessable;
import org.battleplugins.arena.util.BlockUtil;
import org.battleplugins.arena.util.VoidChunkGenerator;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Represents a map for a competition which is live on this server.
 *
 * @param <T> the type of competition this map is for
 */
public class LiveCompetitionMap<T extends Competition<T>> implements ArenaLike, CompetitionMap<T>, PostProcessable {
    @ArenaOption(name = "name", description = "The name of the map.", required = true)
    private String name;

    @ArenaOption(name = "arena", description = "The arena this map is for.", required = true)
    private Arena arena;

    @ArenaOption(name = "type", description = "The type of map.", required = true)
    private MapType type;

    @ArenaOption(name = "world", description = "The world the map is located in.", required = true)
    private String world;

    @ArenaOption(name = "bounds", description = "The bounds of the map.")
    private Bounds bounds;

    @ArenaOption(name = "spawns", description = "The spawn locations.")
    private Spawns spawns;

    private World mapWorld;

    public LiveCompetitionMap() {
    }

    public LiveCompetitionMap(String name, Arena arena, MapType type, String world, @Nullable Bounds bounds, @Nullable Spawns spawns) {
        this.name = name;
        this.arena = arena;
        this.type = type;
        this.world = world;
        this.bounds = bounds;
        this.spawns = spawns;
    }

    @Override
    public void postProcess() {
        this.mapWorld = Bukkit.getWorld(this.world);
        if (this.mapWorld == null) {
            throw new IllegalStateException("World " + this.world + " for map " + this.name + " in arena " + this.arena.getName() + " does not exist!");
        }
    }

    @Override
    public final String getName() {
        return this.name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    @Override
    public final Arena getArena() {
        return this.arena;
    }

    @Override
    public final CompetitionType<T> getCompetitionType() {
        return (CompetitionType<T>) this.arena.getType();
    }

    @Override
    public final MapType getType() {
        return this.type;
    }

    public final void setType(MapType type) {
        this.type = type;
    }

    public final World getWorld() {
        return this.mapWorld;
    }

    @Nullable
    public final Bounds getBounds() {
        return this.bounds;
    }

    public final void setBounds(Bounds bounds) {
        this.bounds = bounds;
    }

    @Nullable
    public final Spawns getSpawns() {
        return this.spawns;
    }

    public final void setSpawns(Spawns spawns) {
        this.spawns = spawns;
    }

    public T createCompetition(Arena arena) {
        return this.getCompetitionType().create(arena, this);
    }

    @Nullable
    public final T createDynamicCompetition(Arena arena) {
        String worldName = "ba-dynamic-" + UUID.randomUUID();
        World world = Bukkit.createWorld(WorldCreator.name(worldName)
                .generator(VoidChunkGenerator.INSTANCE)
                .environment(World.Environment.NORMAL)
                .generateStructures(false)
                .keepSpawnLoaded(TriState.FALSE)
                .type(WorldType.NORMAL)
        );

        if (world == null) {
            return null;
        }

        if (!BlockUtil.copyToWorld(this.mapWorld, world, this.bounds)) {
            return null; // Failed to copy
        }

        LiveCompetitionMap<T> copy = new LiveCompetitionMap<>(this.name, arena, this.type, worldName, this.bounds, this.spawns);
        copy.mapWorld = world;

        return copy.createCompetition(arena);
    }
}
