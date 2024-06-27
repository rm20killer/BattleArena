package org.battleplugins.arena.module.tournaments;

import org.jetbrains.annotations.Nullable;

/**
 * A pair of two contestants battling together.
 *
 * @param contestant1 the first contestant
 * @param contestant2 the second contestant
 */
public record ContestantPair(Contestant contestant1, @Nullable Contestant contestant2) {

    /**
     * Whether the tournament should auto-advance as one of
     * the contestants has a "bye" or exclusion.
     *
     * @return whether the tournament should auto-advance
     */
    public boolean autoAdvance() {
        return this.contestant2 == null;
    }

    public boolean isDone() {
        return this.contestant1.isDone() && (this.contestant2 == null || this.contestant2.isDone());
    }
}