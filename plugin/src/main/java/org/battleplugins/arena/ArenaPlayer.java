package org.battleplugins.arena;

import org.battleplugins.arena.competition.LiveCompetition;
import org.battleplugins.arena.competition.PlayerRole;
import org.battleplugins.arena.competition.PlayerStorage;
import org.battleplugins.arena.event.player.ArenaStatChangeEvent;
import org.battleplugins.arena.stat.ArenaStat;
import org.battleplugins.arena.stat.StatHolder;
import org.battleplugins.arena.team.ArenaTeam;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class ArenaPlayer implements StatHolder {
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
    }

    public Player getPlayer() {
        return this.player;
    }

    public Arena getArena() {
        return this.arena;
    }

    public LiveCompetition<?> getCompetition() {
        return this.competition;
    }

    public PlayerStorage getStorage() {
        return this.storage;
    }

    public PlayerRole getRole() {
        return this.role;
    }

    public void setRole(PlayerRole role) {
        this.role = role;
    }

    public Optional<ArenaTeam> team() {
        return Optional.ofNullable(this.getTeam());
    }

    @Nullable
    public ArenaTeam getTeam() {
        return this.team;
    }

    public void setTeam(@Nullable ArenaTeam team) {
        this.team = team;
    }

    public void remove() {
        this.removeMetadata();
    }

    void setMetadata() {
        this.player.setMetadata(ARENA_PLAYER_META_KEY, new FixedMetadataValue(this.arena.getPlugin(), this));
    }

    void removeMetadata() {
        this.player.removeMetadata(ARENA_PLAYER_META_KEY, this.arena.getPlugin());
    }

    @Override
    public <T> Optional<T> stat(ArenaStat<T> stat) {
        return Optional.ofNullable(this.getStat(stat));
    }

    @Override
    @Nullable
    public <T> T getStat(ArenaStat<T> stat) {
        return (T) this.stats.get(stat);
    }

    @Override
    public <T> void setStat(ArenaStat<T> stat, T value) {
        this.computeStat(stat, oldValue -> value);
    }

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

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T getMetadata(Class<T> metadataClass) {
        return (T) this.metadata.get(metadataClass);
    }

    public <T> Optional<T> metadata(Class<T> metadataClass) {
        return Optional.ofNullable(this.getMetadata(metadataClass));
    }

    public <T> void setMetadata(Class<T> metadataClass, T value) {
        this.metadata.put(metadataClass, value);
    }

    public <T> void removeMetadata(Class<T> metadataClass) {
        this.metadata.remove(metadataClass);
    }

    public void resetState() {
        // TODO: Save stats in a remote location (BattleTracker)
        this.stats.clear();

        this.competition.getTeamManager().leaveTeam(this);
        this.competition.findAndJoinTeamIfApplicable(this);
    }

    @Override
    public String toString() {
        return "ArenaPlayer{" +
                "player=" + this.player +
                '}';
    }

    @Nullable
    public static ArenaPlayer getArenaPlayer(Player player) {
        if (!player.hasMetadata(ARENA_PLAYER_META_KEY)) {
            return null;
        }

        return (ArenaPlayer) player.getMetadata(ARENA_PLAYER_META_KEY).get(0).value();
    }
}
