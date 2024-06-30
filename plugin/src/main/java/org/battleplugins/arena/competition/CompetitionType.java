package org.battleplugins.arena.competition;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.competition.event.Event;
import org.battleplugins.arena.competition.event.LiveEvent;
import org.battleplugins.arena.competition.map.LiveCompetitionMap;
import org.battleplugins.arena.competition.match.LiveMatch;
import org.battleplugins.arena.competition.match.Match;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a competition type.
 *
 * @param <T> the type of competition
 */
public final class CompetitionType<T extends Competition<T>> {
    private static final Map<String, CompetitionType<?>> COMPETITION_TYPES = new HashMap<>();

    public static final CompetitionType<Event> EVENT = new CompetitionType<>("Event", Event.class, LiveEvent::new);
    public static final CompetitionType<Match> MATCH = new CompetitionType<>("Match", Match.class, LiveMatch::new);

    private final Class<T> clazz;
    private final CompetitionFactory<T> factory;

    CompetitionType(String name, Class<T> clazz, CompetitionFactory<T> factory) {
        this.clazz = clazz;
        this.factory = factory;

        COMPETITION_TYPES.put(name, this);
    }

    public Class<T> getCompetitionType() {
        return this.clazz;
    }

    public T create(Arena arena, LiveCompetitionMap map) {
        return this.factory.create(arena, map);
    }

    @Nullable
    public static CompetitionType<?> get(String name) {
        return COMPETITION_TYPES.get(name);
    }

    public static <T extends Competition<T>> CompetitionType<T> create(String name, Class<T> clazz, CompetitionFactory<T> factory) {
        return new CompetitionType<>(name, clazz, factory);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        CompetitionType<?> that = (CompetitionType<?>) object;
        return Objects.equals(this.clazz, that.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.clazz);
    }

    public interface CompetitionFactory<T extends Competition<T>> {

        T create(Arena arena, LiveCompetitionMap map);
    }
}
