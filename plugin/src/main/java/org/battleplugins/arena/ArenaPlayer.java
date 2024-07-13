package org.battleplugins.arena;

import org.battleplugins.arena.competition.LiveCompetition;
import org.battleplugins.arena.competition.PlayerRole;
import org.battleplugins.arena.competition.PlayerStorage;
import org.battleplugins.arena.event.player.ArenaStatChangeEvent;
import org.battleplugins.arena.event.player.ArenaTeamJoinEvent;
import org.battleplugins.arena.event.player.ArenaTeamLeaveEvent;
import org.battleplugins.arena.resolver.Resolvable;
import org.battleplugins.arena.resolver.Resolver;
import org.battleplugins.arena.resolver.ResolverKey;
import org.battleplugins.arena.resolver.ResolverKeys;
import org.battleplugins.arena.resolver.ResolverProvider;
import org.battleplugins.arena.stat.ArenaStat;
import org.battleplugins.arena.stat.ArenaStats;
import org.battleplugins.arena.stat.StatHolder;
import org.battleplugins.arena.team.ArenaTeam;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Represents a player in an active competition.
 */
public class ArenaPlayer implements StatHolder, Resolvable {
    private static final String ARENA_PLAYER_META_KEY = "arena-player";

    private final Player player;
    private final Arena arena;
    private final LiveCompetition<?> competition;

    private final PlayerStorage storage;

    private final Map<ArenaStat<?>, Object> stats = new HashMap<>();
    private final Map<Class<?>, Object> metadata = new HashMap<>();

    private PlayerRole role;

    @Nullable
    private ArenaTeam team;

    public ArenaPlayer(Player player, Arena arena, LiveCompetition<?> competition) {
        this.player = player;
        this.arena = arena;
        this.competition = competition;
        this.storage = new PlayerStorage(this);

        this.setMetadata();

        // Register default stats
        for (ArenaStat<?> stat : ArenaStats.values()) {
            this.stats.put(stat, stat.getDefaultValue());
        }
    }

    /**
     * Returns the {@link Player} associated with this
     * arena player.
     *
     * @return the player associated with this arena player
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Returns the {@link Arena} that this arena player is
     * currently in.
     *
     * @return the arena that this arena player is in
     */
    public Arena getArena() {
        return this.arena;
    }

    /**
     * Returns the {@link LiveCompetition} that this arena player
     * is currently in.
     *
     * @return the live competition that this arena player is in
     */
    public LiveCompetition<?> getCompetition() {
        return this.competition;
    }

    /**
     * Returns the {@link PlayerStorage} which stores information
     * about this player which may need to be restored after the
     * competition.
     *
     * @return the player storage for this player
     */
    public PlayerStorage getStorage() {
        return this.storage;
    }

    /**
     * Returns the role of this player in the competition.
     *
     * @return the role of this player in the competition
     */
    public PlayerRole getRole() {
        return this.role;
    }

    /**
     * Sets the role of this player in the competition.
     *
     * @param role the role of this player in the competition
     */
    public void setRole(PlayerRole role) {
        this.role = role;
    }

    /**
     * Returns the team that this player is on.
     *
     * @return the team that this player is on
     */
    public Optional<ArenaTeam> team() {
        return Optional.ofNullable(this.getTeam());
    }

    /**
     * Returns the team that this player is on.
     *
     * @return the team that this player is on,
     * or null if the player is not on a team
     */
    @Nullable
    public ArenaTeam getTeam() {
        return this.team;
    }

    /**
     * Sets the team that this player is on.
     *
     * @param team the team that this player is on
     */
    public void setTeam(@Nullable ArenaTeam team) {
        if (this.team != null) {
            new ArenaTeamLeaveEvent(this, this.team).callEvent();
        }

        if (team != null) {
            new ArenaTeamJoinEvent(this, team).callEvent();
        }

        this.team = team;
    }

    /**
     * Removes the metadata associated with this player.
     */
    public void remove() {
        this.removeMetadata();
    }

    void setMetadata() {
        this.player.setMetadata(ARENA_PLAYER_META_KEY, new FixedMetadataValue(this.arena.getPlugin(), this));
    }

    void removeMetadata() {
        this.player.removeMetadata(ARENA_PLAYER_META_KEY, this.arena.getPlugin());
    }

    /**
     * Returns the stat value of the given {@link ArenaStat}.
     *
     * @param stat the stat to get
     * @param <T> the type of the stat
     * @return the stat of the given arena stat
     */
    @Override
    public <T> Optional<T> stat(ArenaStat<T> stat) {
        return Optional.ofNullable(this.getStat(stat));
    }

    /**
     * Returns the stat value of the given {@link ArenaStat}.
     *
     * @param stat the stat to get
     * @param <T> the type of the stat
     * @return the stat of the given arena stat, or null if the stat
     *         does not exist or is not set for this player
     */
    @Override
    @Nullable
    public <T> T getStat(ArenaStat<T> stat) {
        return (T) this.stats.get(stat);
    }

    /**
     * Sets the stat value of the given {@link ArenaStat}.
     *
     * @param stat the stat to set
     * @param value the value to set the stat to
     * @param <T> the type of the stat
     */
    @Override
    public <T> void setStat(ArenaStat<T> stat, T value) {
        this.computeStat(stat, oldValue -> value);
    }

    /**
     * Computes the stat value of the given {@link ArenaStat} using the given function.
     *
     * @param stat the stat to compute
     * @param computeFunction the function to compute the stat
     * @param <T> the type of the stat
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> void computeStat(ArenaStat<T> stat, Function<? super T, ? extends T> computeFunction) {
        this.stats.compute(stat, (key, oldValue) -> {
            T newValue = computeFunction.apply((T) oldValue);
            return this.statChange(stat, (T) oldValue, newValue);
        });
    }

    private <T> T statChange(ArenaStat<T> stat, T oldValue, T newValue) {
        ArenaStatChangeEvent<T> event = new ArenaStatChangeEvent<>(this.competition, this, stat, oldValue, newValue);
        this.arena.getEventManager().callEvent(event);
        return event.getNewValue();
    }

    /**
     * Gets a stored metadata value for this player.
     *
     * @param metadataClass the class of the metadata
     * @param <T> the type of the metadata
     * @return the metadata value for this player
     */
    public <T> Optional<T> metadata(Class<T> metadataClass) {
        return Optional.ofNullable(this.getMetadata(metadataClass));
    }

    /**
     * Gets a stored metadata value for this player.
     *
     * @param metadataClass the class of the metadata
     * @param <T> the type of the metadata
     * @return the metadata value for this player, or
     *         null if the metadata does not exist
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T getMetadata(Class<T> metadataClass) {
        return (T) this.metadata.get(metadataClass);
    }

    /**
     * Sets a metadata value for this player.
     *
     * @param metadataClass the class of the metadata
     * @param value the value to set the metadata to
     * @param <T> the type of the metadata
     */
    public <T> void setMetadata(Class<T> metadataClass, T value) {
        this.metadata.put(metadataClass, value);
    }

    /**
     * Removes a metadata value for this player.
     *
     * @param metadataClass the class of the metadata to remove
     * @param <T> the type of the metadata
     */
    public <T> void removeMetadata(Class<T> metadataClass) {
        this.metadata.remove(metadataClass);
    }

    /**
     * Resets the state of this player.
     */
    public void resetState() {
        // TODO: Save stats in a remote location (BattleTracker)
        this.stats.clear();

        this.competition.getTeamManager().leaveTeam(this);
        this.competition.findAndJoinTeamIfApplicable(this);
    }


    @Override
    public String describe() {
        return this.getPlayer().getName();
    }

    @Override
    public Resolver resolve() {
        Resolver.Builder builder = this.competition.resolve().toBuilder()
                .define(ResolverKeys.PLAYER, ResolverProvider.simple(this, this.player::getName));

        if (this.team != null) {
            builder.define(ResolverKeys.TEAM, ResolverProvider.simple(this.team, ArenaTeam::getName, ArenaTeam::getFormattedName));
        }

        for (Map.Entry<ArenaStat<?>, Object> entry : this.stats.entrySet()) {
            ResolverKey<Object> statKey = ResolverKey.create("stat_" + entry.getKey().getKey(), Object.class);
            builder.define(statKey, ResolverProvider.simple(entry.getValue(), String::valueOf));
        }

        return builder.build();
    }

    @Override
    public String toString() {
        return "ArenaPlayer{" +
                "player=" + this.player +
                '}';
    }

    /**
     * Gets an {@link Optional} of the {@link ArenaPlayer} associated with the given player.
     *
     * @param player the player to get the arena player of
     * @return an optional of the arena player associated with the given player
     */
    public static Optional<ArenaPlayer> arenaPlayer(Player player) {
        return Optional.ofNullable(getArenaPlayer(player));
    }

    /**
     * Gets the {@link ArenaPlayer} associated with the given player.
     *
     * @param player the player to get the arena player of
     * @return the arena player associated with the given player, or
     *         null if the player is not in a competition
     */
    @Nullable
    public static ArenaPlayer getArenaPlayer(Player player) {
        if (!player.hasMetadata(ARENA_PLAYER_META_KEY)) {
            return null;
        }

        return (ArenaPlayer) player.getMetadata(ARENA_PLAYER_META_KEY).get(0).value();
    }
}
