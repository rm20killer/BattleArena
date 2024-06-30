package org.battleplugins.arena.competition;

import org.jetbrains.annotations.Nullable;

/**
 * Represents a competition result.
 *
 * @param competition the competition
 * @param result the {@link JoinResult} of the competition
 */
public record CompetitionResult(@Nullable Competition<?> competition, JoinResult result) {
}
